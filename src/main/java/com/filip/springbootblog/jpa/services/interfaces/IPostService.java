package com.filip.springbootblog.jpa.services.interfaces;

import com.filip.springbootblog.jpa.dto.AlphabetDTO;
import com.filip.springbootblog.jpa.dto.CategoryDTO;
import com.filip.springbootblog.jpa.dto.PostDTO;
import com.filip.springbootblog.jpa.dto.TagDTO;
import com.filip.springbootblog.jpa.enums.PostType;
import com.filip.springbootblog.jpa.exceptions.TagNotFoundException;
import com.filip.springbootblog.jpa.models.Category;
import com.filip.springbootblog.jpa.models.Post;
import com.filip.springbootblog.jpa.models.PostImage;
import com.filip.springbootblog.jpa.models.PostMeta;
import com.filip.springbootblog.jpa.models.Tag;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface IPostService {

    Post add(PostDTO postDTO);

    Post getPost(String postName);

    PostMeta getPostMetaById(Long postId);

    PostMeta buildTwitterMetaTagsForDisplay(Post post);

    Page<Post> getPosts(Integer pageNumber, Integer pageSize);

    @Transactional
    Post update(PostDTO postDTO);

    @Transactional(readOnly = true)
    List<Post> getPostsByUserLikes(Long userId);

    @Transactional(readOnly = true)
    List<Post> getPagedLikedPosts(long userId, int pageNumber, int pageSize);

    @Transactional
    int addPostLike(long userId, long postId);

    Post getPostById(Long postId);

    @Transactional(readOnly = true)
    Page<Post> getPublishedPosts(Integer pageNumber, Integer pageSize);

    @Transactional(readOnly = true)
    List<Post> getAllPosts();

    @Transactional(readOnly = true)
    List<Post> getAdminRecentPosts();

    @Transactional(readOnly = true)
    List<Post> getAllPublishedPostsByPostType(PostType postType);

    @Transactional(readOnly = true)
    Page<Post> getPagedPostsByPostType(PostType postType, int pageNumber, int pageSize);

    @Transactional(readOnly = true)
    List<Post> getAllPublishedPosts();

    Optional<Post> getOneMostRecent();

    @Transactional(readOnly = true)
    List<Post> getPostsWithDetail();

    @Transactional
    Tag createTag(TagDTO tagDTO);

    @Transactional
    Tag updateTag(TagDTO tagDTO);

    @Transactional
    void deleteTag(TagDTO tagDTO, List<Post> posts);

    @Transactional(readOnly = true)
    Set<TagDTO> getTagDTOs();

    @Transactional(readOnly = true)
    List<TagDTO> getTagCloud(int tagCount);

    @Transactional(readOnly = true)
    List<AlphabetDTO> getAlphaLInks();

    @Transactional(readOnly = true)
    List<PostDTO> getAlphaPosts();

    boolean canUpdatePost(Authentication authentication, Long postId);

    @Transactional(readOnly = true)
    List<String> getTagValues();

    Set<TagDTO> getTagDTOs(Long postId);

    List<Post> getAllPostsByTagId(long tagId);

    @Transactional(readOnly = true)
    List<PostImage> getAllPostImages();

    @Transactional(readOnly = true)
    List<PostImage> getPostImages(long postId);

    @Transactional
    PostImage addImage(PostImage image);

    @Transactional(readOnly = true)
    PostImage getPostImage(long imageId);

    @Transactional(readOnly = true)
    Category getCategory(String categoryValue);

    @SuppressWarnings("JpaQueryApiInspection")
    @Transactional(readOnly = true)
    List<CategoryDTO> getCategoryCounts();

    @SuppressWarnings("JpaQueryApiInspection")
    @Transactional(readOnly = true)
    List<CategoryDTO> getCategoryCounts(int categoryCount);

    @Transactional(readOnly = true)
    List<CategoryDTO> getAdminSelectionCategories();

    @Transactional(readOnly = true)
    List<CategoryDTO> getAdminCategories();

    @Transactional(readOnly = true)
    List<Category> getAllCategories();

    @Transactional
    Category createCategory(CategoryDTO categoryDTO);

    @Transactional
    Category updateCategory(CategoryDTO categoryDTO);

    @Transactional
    void deleteCategory(CategoryDTO categoryDTO, List<Post> posts);

    @Transactional(readOnly = true)
    Category getCategoryById(long categoryId);

    Tag getTag(String tagValue) throws TagNotFoundException;

    Page<Post> getPublishedPostsByTagId(long tagId, int pageNumber, int pageSize);

    @Transactional(readOnly = true)
    List<Post> getAllPostsByCategoryId(long categoryId);

    List<Post> getPublishedPostsByTagId(long tagId);

    void deleteImage(PostImage image);

}
