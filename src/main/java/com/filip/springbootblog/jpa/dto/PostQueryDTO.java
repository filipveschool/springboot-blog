package com.filip.springbootblog.jpa.dto;


import com.filip.springbootblog.jpa.enums.PostType;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.NotEmpty;

@Getter
@Setter
public class PostQueryDTO implements java.io.Serializable {

    private static final long serialVersionUID = -8106151946403787355L;

    public PostQueryDTO() {
    }

    @NotEmpty
    private String query;

    private PostType postType;

    public PostQueryDTO(String query, PostType postType) {
        this.query = query;
        this.postType = postType;
    }

    public PostQueryDTO(String query) {
        this.query = query;
        this.postType = PostType.UNDEFINED;
    }
}
