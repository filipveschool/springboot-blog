package com.filip.springbootblog.jpa.dto;

import com.filip.springbootblog.jpa.models.Category;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class CategoryDTO implements Serializable {


    private static final long serialVersionUID = -615657461752604322L;


    private Long categoryId = 1L;
    private String categoryValue;
    private int categoryCount = 0;
    private Boolean isActive = true;
    private Boolean isDefault = true;

    public CategoryDTO() {
    }

    public CategoryDTO(String categoryValue) {
        this.categoryValue = categoryValue;
    }


    public CategoryDTO(Long categoryId, String categoryValue, int categoryCount, Boolean isActive, Boolean isDefault) {
        this.categoryId = categoryId;
        this.categoryValue = categoryValue;
        this.categoryCount = categoryCount;
        this.isActive = isActive;
        this.isDefault = isDefault;
    }

    public CategoryDTO(Category category) {
        this.categoryId = category.getCategoryId();
        this.categoryValue = category.getCategoryValue();
        this.categoryCount = category.getPosts().size();
        this.isActive = category.getIsActive();
        this.isDefault = category.getIsDefault();
    }

}
