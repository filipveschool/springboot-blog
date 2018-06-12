package com.filip.springbootblog.jpa.repositories;

import com.filip.springbootblog.jpa.models.PostMeta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostMetaRepository extends JpaRepository<PostMeta, Long> {

    @Override
    List<PostMeta> findAll();

    PostMeta findByPostId(Long postId);
}
