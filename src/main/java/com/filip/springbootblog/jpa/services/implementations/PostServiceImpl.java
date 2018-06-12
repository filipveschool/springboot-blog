package com.filip.springbootblog.jpa.services.implementations;

import com.filip.springbootblog.jpa.annotations.CachePostUpdate;
import com.filip.springbootblog.jpa.common.ApplicationSettings;
import com.filip.springbootblog.jpa.dto.AlphabetDTO;
import com.filip.springbootblog.jpa.dto.CategoryDTO;
import com.filip.springbootblog.jpa.dto.PostDTO;
import com.filip.springbootblog.jpa.dto.TagDTO;
import com.filip.springbootblog.jpa.enums.ContentType;
import com.filip.springbootblog.jpa.enums.PostDisplayType;
import com.filip.springbootblog.jpa.enums.PostType;
import com.filip.springbootblog.jpa.enums.TwitterCardType;
import com.filip.springbootblog.jpa.exceptions.CategoryNotFoundException;
import com.filip.springbootblog.jpa.exceptions.PostNotFoundException;
import com.filip.springbootblog.jpa.exceptions.TagNotFoundException;
import com.filip.springbootblog.jpa.models.Category;
import com.filip.springbootblog.jpa.models.CurrentUser;
import com.filip.springbootblog.jpa.models.Like;
import com.filip.springbootblog.jpa.models.Post;
import com.filip.springbootblog.jpa.models.PostImage;
import com.filip.springbootblog.jpa.models.PostMeta;
import com.filip.springbootblog.jpa.models.Tag;
import com.filip.springbootblog.jpa.repositories.CategoryRepository;
import com.filip.springbootblog.jpa.repositories.LikeRepository;
import com.filip.springbootblog.jpa.repositories.PostImageRepository;
import com.filip.springbootblog.jpa.repositories.PostMetaRepository;
import com.filip.springbootblog.jpa.repositories.PostRepository;
import com.filip.springbootblog.jpa.repositories.TagRepository;
import com.filip.springbootblog.jpa.services.interfaces.IPostService;
import com.filip.springbootblog.jpa.utils.PostUtils;
import com.google.common.collect.Lists;
import javafx.geometry.Pos;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Comparator.comparing;

@Service("postService")
@Transactional
@CacheConfig(cacheNames = "posts")
@Slf4j
public class PostServiceImpl implements IPostService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private LikeRepository likeRepository;

    @Autowired
    private PostMetaRepository postMetaRepository;

    @Autowired
    private PostImageRepository postImageRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ApplicationSettings applicationSettings;

    @Autowired
    private CacheManager cacheManager;

    @CachePostUpdate
    @Override
    public Post add(PostDTO postDTO) {

        Post post = postRepository.save(PostUtils.postDtoToPost(postDTO));

        if (postDTO.getTags() != null) {

            saveNewTagsToDataBase(postDTO);

            post.setTags(new HashSet<>());
            for (TagDTO tagDTO : postDTO.getTags()) {
                Tag tag = tagRepository.findByTagValueIgnoreCase(tagDTO.getTagValue());
                post.getTags().add(tag);
            }
        }

        Category category = categoryRepository.findByCategoryId(postDTO.getCategoryId());
        post.setCategory(category);

        return post;
    }

    @Cacheable(key = "#postName")
    @Override
    public Post getPost(String postName) {
        Post found = postRepository.findByPostNameIgnoreCase(postName);
        if (found == null) {
            log.debug("No post found with name: {}", postName);
            throw new PostNotFoundException("No post found with name: " + postName);
        } else {
            populatePostImages(found);
        }

        return found;
    }

    @Override
    public PostMeta getPostMetaById(Long postId) {
        return postMetaRepository.findByPostId(postId);
    }

    @Override
    public PostMeta buildTwitterMetaTagsForDisplay(Post post) {
        PostMeta postMeta = post.getPostMeta();
        if (!postMeta.getTwitterCardType().equals(TwitterCardType.NONE)) {
            String twitterSite = applicationSettings.getTwitterSite();
            String twitterUrl = String.format("%s/post/%s", applicationSettings.getBaseUrl(), post.getPostName());
            String twitterImage = String.format("%s%s", applicationSettings.getBaseUrl(), postMeta.getTwitterImage());
            return PostMeta.getBuilder(
                    postMeta.getTwitterCardType(),
                    post.getPostTitle(),
                    twitterSite,
                    postMeta.getTwitterCreator())
                    .twitterDescription(postMeta.getTwitterDescription())
                    .twitterImage(twitterImage)
                    .twitterUrl(twitterUrl)
                    .build();
        } else {
            return null;
        }
    }

    @Override
    public Page<Post> getPosts(Integer pageNumber, Integer pageSize) {
        return postRepository.findAll(PageRequest.of(pageNumber, pageSize, sortByPostDateDesc()));
    }

    @CachePostUpdate
    @Override
    public Post update(PostDTO postDTO) {
        Post post = postRepository.findByPostId(postDTO.getPostId());
        post.update(postDTO.getPostTitle(), postDTO.getPostContent(), postDTO.getIsPublished(), postDTO.getDisplayType());

        saveNewTagsToDataBase(postDTO);

        post.getTags().clear();
        for (TagDTO tagDTO : postDTO.getTags()) {
            Tag tag = tagRepository.findByTagValueIgnoreCase(tagDTO.getTagValue());

            if (!post.getTags().contains(tag))
                post.getTags().add(tag);
        }

        Category category = categoryRepository.findByCategoryId(postDTO.getCategoryId());
        post.setCategory(category);

        return post;
    }

    @Override
    public List<Post> getPostsByUserLikes(Long userId) {
        List<Post> posts = null;
        List<Long> likedPostIds = likeRepository.findLikedPostIds(userId);

        if (likedPostIds.size() == 0) {
            return null;
        } else {
            posts = postRepository.findByPostIds(likedPostIds);
            /*
              posts = em.createNamedQuery("Post.getByPostIds", Post.class)
                    .setParameter("postIds", likeRepository.findLikedPostIds(userId))
                    .getResultList();
             */
        }

        return posts;
    }

    @Override
    public List<Post> getPagedLikedPosts(long userId, int pageNumber, int pageSize) {
        /*
         List<Post> posts = em.createNamedQuery("Post.getByPostIds", Post.class)
                .setParameter("postIds", likeRepository.findLikedPostIds(userId))
                .setFirstResult(pageNumber * pageSize)
                .setMaxResults(pageSize)
                .getResultList();
         */
        return postRepository.findByPostIds(likeRepository.findLikedPostIds(userId));
    }

    @Override
    public int addPostLike(long userId, long postId) {
        int incrementValue = 1;
        Post post = postRepository.findByPostId(postId);
        Optional<Long> likeId = likeRepository.findPostLikeIdByUserId(userId, postId);
        if (likeId.isPresent()) {
            incrementValue = -1;
            likeRepository.deleteById(likeId.get());
        } else {
            Like like = new Like();
            like.setUserId(userId);
            like.setItemId(postId);
            like.setContentTypeId(ContentType.POST);
            likeRepository.save(like);
        }
        post.updateLikes(incrementValue);
        clearPostCaches();
        return incrementValue;
    }

    @Cacheable(key = "#postId")
    @Override
    public Post getPostById(Long postId) {
        Post found = postRepository.findByPostId(postId);
        if (found == null) {
            log.debug("No post found with id: {}", postId);
            throw new PostNotFoundException("No post found with id: " + postId);
        }
        populatePostImages(found);
        return found;
    }

    private void populatePostImages(Post post) {
        try {
            if (post.getDisplayType().equals(PostDisplayType.MULTIPHOTO_POST))
                post.setPostImages(this.getPostImages(post.getPostId()));
            if (post.getDisplayType().equals(PostDisplayType.SINGLEPHOTO_POST))
                post.setSingleImage(this.getPostImages(post.getPostId()).get(0));
        } catch (Exception e) {
            log.info(String.format("Image Retrieval Error for Post ID:%s Title: %s", String.valueOf(post.getPostId()), post.getPostTitle()));
        }
    }

    @Cacheable(cacheNames = "pagedPosts",
            key = "#pageNumber.toString().concat('-').concat(#pageSize.toString())")
    @Override
    public Page<Post> getPublishedPosts(Integer pageNumber, Integer pageSize) {
        return postRepository.findByIsPublishedTrue(PageRequest.of(pageNumber, pageSize, sortByPostDateDesc()));
    }

    @Override
    public List<Post> getAllPosts() {
        return postRepository.findAll(sortByPostDateDesc());
    }

    @Override
    public List<Post> getAdminRecentPosts() {
        return postRepository.findFirst25ByOOrderByPostDateDesc(sortByPostDateDesc());
    }

    @Override
    public List<Post> getAllPublishedPostsByPostType(PostType postType) {
        return postRepository.findAllPublishedByPostType(postType);
    }

    @Override
    public Page<Post> getPagedPostsByPostType(PostType postType, int pageNumber, int pageSize) {
        return postRepository.findPublishedByPostTypePaged(postType, PageRequest.of(pageNumber, pageSize, sortByPostDateDesc()));
    }

    @Override
    public List<Post> getAllPublishedPosts() {
        return postRepository.findByIsPublishedTrue(sortByPostDateDesc());
    }

    @Override
    public Optional<Post> getOneMostRecent() {
        log.debug("Getting most recent post");
        Page<Post> posts = postRepository.findAll(PageRequest.of(0, 1, sortByPostDateDesc()));
        if (posts.getContent().isEmpty()) {
            log.debug("No documents");
            return Optional.empty();
        } else {
            Post post = posts.getContent().get(0);
            log.trace("Returning {}", post);
            return Optional.of(post);
        }
    }

    @Override
    public List<Post> getPostsWithDetail() {
        return postRepository.findAllWithDetail();
    }

    @Override
    public Tag createTag(TagDTO tagDTO) {
        Tag tag = tagRepository.findByTagValueIgnoreCase(tagDTO.getTagValue());
        if (tag == null) {
            tag = new Tag(tagDTO.getTagValue());
            tagRepository.save(tag);
        }
        return tag;
    }

    @Override
    public Tag updateTag(TagDTO tagDTO) {
        Tag tag = tagRepository.findById(tagDTO.getTagId()).get();
        tag.setTagValue(tagDTO.getTagValue());
        return tag;
    }

    @Override
    public void deleteTag(TagDTO tagDTO, List<Post> posts) {
        if (posts != null) {
            Optional<Tag> tag = tagRepository.findById(tagDTO.getTagId());
            for (Post post : posts) {
                post.getTags().remove(tag.get());
            }
        }
        tagRepository.deleteById(tagDTO.getTagId());
    }

    @Override
    public Set<TagDTO> getTagDTOs() {
        return null;
    }

    @Cacheable(cacheNames = "tagCloud", key = "#root.methodName.concat('-').concat(#tagCount.toString())")
    @Override
    public List<TagDTO> getTagCloud(int tagCount) {
//        List<Tag> tagcloud = em.createNamedQuery("getTagCloud", Tag.class)
//                .getResultList();

        List<Tag> tagcloud = tagRepository.getTagCloud();


        int _tagCount = tagCount > 0 ? tagCount : Integer.MAX_VALUE;
        return tagcloud
                .stream()
                .filter(t -> t.getPosts().size() > 0)
                .limit(_tagCount)
                .map(TagDTO::new)
                .sorted(comparing(TagDTO::getTagValue))
                .collect(Collectors.toList());
    }

    @Override
    public List<AlphabetDTO> getAlphaLInks() {
        char[] alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();

        // Example: 12AGHJLM
        String activeAlphas = postRepository.getAlphaLinkString();

        List<AlphabetDTO> alphaLinks = new ArrayList<>();

        // Iterate over alphabet char Array, set AlphabetDTO.active if char in activeAlphas
        for (char c : alphabet)
            alphaLinks.add(new AlphabetDTO(String.valueOf(c), activeAlphas.indexOf(c) >= 0));

        // add AlphabetDTO record for "0-9", set active if any digits in activeAlphas String
        alphaLinks.add(new AlphabetDTO("0-9", activeAlphas.matches(".*\\d+.*")));

        // sort AlphabetDTO List, "0-9" followed by alphabet
        alphaLinks.sort(Comparator.comparing(AlphabetDTO::getAlphaCharacter));
//Collections.sort(alphaLinks, (o1, o2) ->
//                o1.getAlphaCharacter().compareTo(o2.getAlphaCharacter()));

        // All AlphabetDTO items returned with true/false if contain links
        return alphaLinks;
    }

    @Override
    public List<PostDTO> getAlphaPosts() {
        List<Post> posts = Lists.newArrayList(postRepository.findByIsPublishedTrue(sortByPostDateDesc()));

        // converting all posts to postDTO objects
        //
        // 1) post titles starting with a digit assigned "09" alphaKey
        // 2) postDTO list adds all post titles starting with letter, assigned title firstLetter as alphaKey
        // 3) for NixMash Spring Demo site, Changelists do not appear in A-Z listing

        List<PostDTO> postDTOs = posts
                .stream()
                .filter(p -> Character.isDigit(p.getPostTitle().charAt(0)))
                .map(PostDTO::buildAlphaNumericTitles)
                .sorted(byfirstLetter)
                .collect(Collectors.toList());

        postDTOs.addAll(
                posts
                        .stream()
                        .filter(p -> Character.isAlphabetic(p.getPostTitle().charAt(0)) && !p.getPostTitle().startsWith("Changelist"))
                        .map(PostDTO::buildAlphaTitles)
                        .sorted(byfirstLetter)
                        .collect(Collectors.toList()));

        return postDTOs;
    }

    @Override
    public boolean canUpdatePost(Authentication authentication, Long postId) {
        if (authentication instanceof AnonymousAuthenticationToken)
            return false;

        CurrentUser currentUser = (CurrentUser) authentication.getPrincipal();

        Post post = getPostById(postId);

        Long postUserId = post.getUserId();

        return currentUser.getId().equals(postUserId);
    }

    @Override
    public List<String> getTagValues() {
        Set<Tag> tags = tagRepository.findAll();
        return PostUtils.tagsToTagValues(tags);
    }

    @Override
    public Set<TagDTO> getTagDTOs(Long postId) {
        Set<Tag> tags = tagRepository.findAll();
        return PostUtils.tagsToTagDTOs(tags);
    }

    @Override
    public List<Post> getAllPostsByTagId(long tagId) {
        return postRepository.findAllByTagId(tagId);
    }

    @Override
    public List<PostImage> getAllPostImages() {
        return Lists.newArrayList(postImageRepository.findAll());
    }

    @Override
    public List<PostImage> getPostImages(long postId) {
        List<PostImage> images = Lists.newArrayList(postImageRepository.findByPostId(postId));
        for (PostImage image : images) {
            image.setUrl(applicationSettings.getPostImageUrlRoot());
        }
        return images;
    }

    @Override
    public PostImage addImage(PostImage image) {
        return postImageRepository.saveAndFlush(image);
    }

    @Override
    public PostImage getPostImage(long imageId) {
        return postImageRepository.findById(imageId).orElseThrow(() -> new IllegalArgumentException("Post image not found"));
    }

    @Override
    public Category getCategory(String categoryValue) {
        Category found = categoryRepository.findByCategoryValueIgnoreCase(categoryValue);
        if (found == null) {
            log.info("No category found with id: {}", categoryValue);
            throw new CategoryNotFoundException("No category found with id: " + categoryValue);
        }
        return found;
    }

    @Override
    public List<CategoryDTO> getCategoryCounts() {
        return getCategoryCounts(-1);
    }

    @Override
    public List<CategoryDTO> getCategoryCounts(int categoryCount) {
//        List<Category> categories = em.createNamedQuery("getCategoryCounts", Category.class)
//                .getResultList();

        List<Category> categories = categoryRepository.getCategoryCounts();

        return categories
                .stream()
                .filter(c -> c.getPosts().size() > 0)
                .limit(categoryCount > 0 ? categoryCount : Long.MAX_VALUE)
                .map(CategoryDTO::new)
                .sorted(comparing(CategoryDTO::getCategoryValue))
                .collect(Collectors.toList());
    }

    @Override
    public List<CategoryDTO> getAdminSelectionCategories() {
        List<Category> categories = categoryRepository.findByIsActiveTrue(sortByCategoryAsc());
        return PostUtils.categoriesToCategoryDTOs(categories);
    }

    @Override
    public List<CategoryDTO> getAdminCategories() {
        List<Category> categories = categoryRepository.findAll(sortByCategoryAsc());
        for (Category category : categories) {
            category.setCategoryCount(getAllPostsByCategoryId(category.getCategoryId()).size());
        }
        return PostUtils.categoriesToCategoryDTOs(categories);
    }

    @Override
    public List<Category> getAllCategories() {
        return categoryRepository.findAll(sortByCategoryAsc());
    }

    @Override
    public Category createCategory(CategoryDTO categoryDTO) {
        Category category = categoryRepository.findByCategoryValueIgnoreCase(categoryDTO.getCategoryValue());
        if (category == null) {
            category = new Category();
            category.setCategoryValue(categoryDTO.getCategoryValue());
            category.setIsActive(true);
            category.setIsDefault(false);
            categoryRepository.save(category);
        }
        return category;
    }

    @Override
    public Category updateCategory(CategoryDTO categoryDTO) {
        Category category = categoryRepository.findByCategoryId(categoryDTO.getCategoryId());
        if (categoryDTO.getCategoryId() > 1) {
            if (categoryDTO.getIsDefault().equals(true))
                clearCategoryDefaults();
            category.update(categoryDTO.getCategoryValue(), categoryDTO.getIsActive(), categoryDTO.getIsDefault());
        }
        return category;
    }

    @Override
    public void deleteCategory(CategoryDTO categoryDTO, List<Post> posts) {
        if (categoryDTO.getCategoryId() > 1) {
            if (posts != null) {
                Category unCategorizedCategory = categoryRepository.findByCategoryId(1L);
                for (Post post : posts) {
                    post.setCategory(unCategorizedCategory);
                }
            }
            categoryRepository.deleteById(categoryDTO.getCategoryId());
        }
    }

    @Transactional
    void clearCategoryDefaults() {
        Iterable<Category> cats = categoryRepository.findAll();
        for (Category cat : cats) {
            cat.clearDefault();
        }
    }

    @Override
    public Category getCategoryById(long categoryId) {
        return categoryRepository.findByCategoryId(categoryId);
    }

    @Override
    public Tag getTag(String tagValue) {
        Tag found = tagRepository.findByTagValueIgnoreCase(tagValue);
        if (found == null) {
            log.info("No tag found with id: {}", tagValue);
            throw new TagNotFoundException("No tag found with id: " + tagValue);
        }
        return found;
    }

    @Override
    public Page<Post> getPublishedPostsByTagId(long tagId, int pageNumber, int pageSize) {
        return postRepository.findPagedPublishedByTagId(tagId,
                PageRequest.of(pageNumber, pageSize, sortByPostDateDesc()));
    }

    @Override
    public List<Post> getAllPostsByCategoryId(long categoryId) {
        return postRepository.findAllByCategoryId(categoryId);
    }

    @Override
    public List<Post> getPublishedPostsByTagId(long tagId) {
        return postRepository.findAllPublishedByTagId(tagId);
    }

    @Override
    public void deleteImage(PostImage image) {
        postImageRepository.delete(image);
    }


    @Transactional
    void saveNewTagsToDataBase(PostDTO postDTO) {
        for (TagDTO tagDTO : postDTO.getTags()) {
            Tag tag = tagRepository.findByTagValueIgnoreCase(tagDTO.getTagValue());
            if (tag == null) {
                tag = new Tag(tagDTO.getTagValue());
                tagRepository.save(tag);
            }
        }
    }


    //region Utility methods

    public Sort sortByPostDateDesc() {
        return new Sort(Sort.Direction.DESC, "postDate");
    }

    public Sort sortByCategoryAsc() {
        return new Sort(Sort.Direction.ASC, "categoryValue");
    }


    public void clearPostCaches() {
        Objects.requireNonNull(cacheManager.getCache("posts")).clear();
        Objects.requireNonNull(cacheManager.getCache("pagedPosts")).clear();
    }

    private Comparator<PostDTO> byfirstLetter = (e1, e2) -> e1
            .getPostTitle().compareTo(e2.getPostTitle());
    //endregion
}
