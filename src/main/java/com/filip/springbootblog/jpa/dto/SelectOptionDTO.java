package com.filip.springbootblog.jpa.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class SelectOptionDTO implements Serializable {


    private static final long serialVersionUID = -5596921468209602927L;

    private String label;
    private String value;
    private Boolean selected;

    public SelectOptionDTO(String label, String value, Boolean selected) {
        this.label = label;
        this.value = value;
        this.selected = selected;
    }
}
