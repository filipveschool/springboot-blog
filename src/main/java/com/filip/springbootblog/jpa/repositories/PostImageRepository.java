package com.filip.springbootblog.jpa.repositories;

import com.filip.springbootblog.jpa.models.PostImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostImageRepository extends JpaRepository<PostImage, Long> {
    @Override
    List<PostImage> findAll();

    List<PostImage> findByPostId(long postId);
}
