package com.filip.springbootblog.jpa.services.implementations;

import com.filip.springbootblog.jpa.common.ApplicationSettings;
import com.filip.springbootblog.jpa.dto.AlphabetDTO;
import com.filip.springbootblog.jpa.dto.CategoryDTO;
import com.filip.springbootblog.jpa.dto.PostDTO;
import com.filip.springbootblog.jpa.dto.TagDTO;
import com.filip.springbootblog.jpa.enums.PostType;
import com.filip.springbootblog.jpa.models.Category;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;
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

    @Override
    public Post add(PostDTO postDTO) {
        return null;
    }

    @Override
    public Post getPost(String postName) {
        return null;
    }

    @Override
    public PostMeta getPostMetaById(Long postId) {
        return null;
    }

    @Override
    public PostMeta buildTwitterMetaTagsForDisplay(Post post) {
        return null;
    }

    @Override
    public Page<Post> getPosts(Integer pageNumber, Integer pageSize) {
        return null;
    }

    @Override
    public Post update(PostDTO postDTO) {
        return null;
    }

    @Override
    public List<Post> getPostsByUserLikes(Long userId) {
        return null;
    }

    @Override
    public List<Post> getPagedLikedPosts(long userId, int pageNumber, int pageSize) {
        return null;
    }

    @Override
    public int addPostLike(long userId, long postId) {
        return 0;
    }

    @Override
    public Post getPostById(Long postId) {
        return null;
    }

    @Override
    public Page<Post> getPublishedPosts(Integer pageNumber, Integer pageSize) {
        return null;
    }

    @Override
    public List<Post> getAllPosts() {
        return null;
    }

    @Override
    public List<Post> getAdminRecentPosts() {
        return null;
    }

    @Override
    public List<Post> getAllPublishedPostsByPostType(PostType postType) {
        return null;
    }

    @Override
    public Page<Post> getPagedPostsByPostType(PostType postType, int pageNumber, int pageSize) {
        return null;
    }

    @Override
    public List<Post> getAllPublishedPosts() {
        return null;
    }

    @Override
    public Optional<Post> getOneMostRecent() {
        return Optional.empty();
    }

    @Override
    public List<Post> getPostsWithDetail() {
        return null;
    }

    @Override
    public Tag createTag(TagDTO tagDTO) {
        return null;
    }

    @Override
    public Tag updateTag(TagDTO tagDTO) {
        return null;
    }

    @Override
    public void deleteTag(TagDTO tagDTO, List<Post> posts) {

    }

    @Override
    public Set<TagDTO> getTagDTOs() {
        return null;
    }

    @Override
    public List<TagDTO> getTagCloud(int tagCount) {
        return null;
    }

    @Override
    public List<AlphabetDTO> getAlphaLInks() {
        return null;
    }

    @Override
    public List<PostDTO> getAlphaPosts() {
        return null;
    }

    @Override
    public boolean canUpdatePost(Authentication authentication, Long postId) {
        return false;
    }

    @Override
    public List<String> getTagValues() {
        return null;
    }

    @Override
    public Set<TagDTO> getTagDTOs(Long postId) {
        return null;
    }

    @Override
    public List<Post> getAllPostsByTagId(long tagId) {
        return null;
    }

    @Override
    public List<PostImage> getAllPostImages() {
        return null;
    }

    @Override
    public List<PostImage> getPostImages(long postId) {
        return null;
    }

    @Override
    public PostImage addImage(PostImage image) {
        return null;
    }

    @Override
    public PostImage getPostImage(long imageId) {
        return null;
    }

    @Override
    public Category getCategory(String categoryValue) {
        return null;
    }

    @Override
    public List<CategoryDTO> getCategoryCounts() {
        return null;
    }

    @Override
    public List<CategoryDTO> getCategoryCounts(int categoryCount) {
        return null;
    }

    @Override
    public List<CategoryDTO> getAdminSelectionCategories() {
        return null;
    }

    @Override
    public List<CategoryDTO> getAdminCategories() {
        return null;
    }

    @Override
    public List<Category> getAllCategories() {
        return null;
    }

    @Override
    public Category createCategory(CategoryDTO categoryDTO) {
        return null;
    }

    @Override
    public Category updateCategory(CategoryDTO categoryDTO) {
        return null;
    }

    @Override
    public void deleteCategory(CategoryDTO categoryDTO, List<Post> posts) {

    }

    @Override
    public Category getCategoryById(long categoryId) {
        return null;
    }

    @Override
    public Tag getTag(String tagValue) {
        return null;
    }

    @Override
    public Page<Post> getPublishedPostsByTagId(long tagId, int pageNumber, int pageSize) {
        return null;
    }

    @Override
    public List<Post> getAllPostsByCategoryId(long categoryId) {
        return null;
    }

    @Override
    public List<Post> getPublishedPostsByTagId(long tagId) {
        return null;
    }

    @Override
    public void deleteImage(PostImage image) {

    }
}
