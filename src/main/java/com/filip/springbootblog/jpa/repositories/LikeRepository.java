package com.filip.springbootblog.jpa.repositories;

import com.filip.springbootblog.jpa.models.Like;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LikeRepository extends JpaRepository<Like, Long> {

    List<Like> findByUserIdAndContentTypeId(Long userId, int contentTypeId);

    List<Like> findByContentTypeId(int contentTypeId);

    @Override
    List<Like> findAll();

    @Query("select l.itemId from Like l where l.contentTypeId = 1 and l.userId = ?1")
    List<Long> findLikedPostIds(long userId);

    @Query("select l.likeId from Like l where l.contentTypeId = 1 and l.userId = ?1 and l.itemId = ?2")
    Optional<Long> findPostLikeIdByUserId(Long userId, long postId);
}
