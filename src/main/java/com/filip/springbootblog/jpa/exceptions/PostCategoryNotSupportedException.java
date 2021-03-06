package com.filip.springbootblog.jpa.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class PostCategoryNotSupportedException extends RuntimeException {

    private static final long serialVersionUID = -1402436282978052954L;

    public PostCategoryNotSupportedException() {
        super();
    }

}
