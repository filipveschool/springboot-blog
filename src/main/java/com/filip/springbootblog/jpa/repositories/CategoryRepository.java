package com.filip.springbootblog.jpa.repositories;

import com.filip.springbootblog.jpa.models.Category;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    Category findByCategoryValueIgnoreCase(String categoryValue);

    @Override
    List<Category> findAll(Sort sort);

    List<Category> findByIsActiveTrue(Sort sort);

    Category findByCategoryId(Long categoryId);
}
