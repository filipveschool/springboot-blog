package com.filip.springbootblog.jpa.dto;

import com.filip.springbootblog.jpa.models.Tag;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class TagDTO implements Serializable {

    private static final long serialVersionUID = -6032607328478129556L;

    private long tagId = -1;

    private String tagValue;

    private int tagCount = 0;

    public TagDTO() {
    }

    public TagDTO(String tagValue) {
        this.tagValue = tagValue;
    }


    public TagDTO(long tagId, String tagValue) {
        this.tagId = tagId;
        this.tagValue = tagValue;
    }

    public TagDTO(Tag tag) {
        this.tagId = tag.getTagId();
        this.tagValue = tag.getTagValue();
        this.tagCount = tag.getPosts().size();
    }
}
