package com.filip.springbootblog.jpa.repositories;

import com.filip.springbootblog.jpa.enums.PostType;
import com.filip.springbootblog.jpa.models.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends PagingAndSortingRepository<Post, Long> {

    Post findByPostId(Long postId);

    @Query("select distinct p from Post p left join fetch p.tags t")
    List<Post> findAllWithDetail();

    Post findByPostNameIgnoreCase(String postName);

    @Query("select distinct p from Post p left join p.tags t where p.isPublished = true and t.tagId = ?1")
    Page<Post> findPagedPublishedByTagId(Long tagId, Pageable pageable);

    @Query("select distinct p from Post p left join p.tags t where p.isPublished = true and t.tagId = ?1")
    List<Post> findAllPublishedByTagId(long tagId);

    @Query("select distinct p from Post p left join p.tags t where t.tagId = ?1")
    List<Post> findAllByTagId(long tagId);

    @Query("select distinct p from Post p left join p.category c where c.categoryId = ?1")
    List<Post> findAllByCategoryId(long categoryId);

    List<Post> findAll(Sort sort);

    List<Post> findFirst25ByOOrderByPostDateDesc(Sort sort);

    List<Post> findByIsPublishedTrue(Sort sort);

    Page<Post> findByIsPublishedTrue(Pageable pageable);

    @Query(value = "SELECT " +
            "GROUP_CONCAT(distinct(upper(substr(post_title,1,1))) SEPARATOR '')" +
            " FROM posts WHERE is_published = true", nativeQuery = true)
    String getAlphaLinkString();

    @Query("select distinct p from Post p where p.displayType = 'SINGLEPHOTO_POST'")
    List<Post> findSinglePhotoPosts(Sort sort);

    @Query("select distinct p from Post p left join p.tags t where p.isPublished = true and p.postType = ?1")
    List<Post> findAllPublishedByPostType(PostType postType);

    @Query("select distinct p from Post p left join p.tags t where p.isPublished = true and p.postType = ?1")
    Page<Post> findPublishedByPostTypePaged(PostType postType, Pageable pageable);


}
