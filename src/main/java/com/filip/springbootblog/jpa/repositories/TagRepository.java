package com.filip.springbootblog.jpa.repositories;

import com.filip.springbootblog.jpa.models.Tag;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface TagRepository extends CrudRepository<Tag, Long> {

    Tag findByTagValueIgnoreCase(String tagValue);

    @Override
    Set<Tag> findAll();
}
