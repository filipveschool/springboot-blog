package com.filip.springbootblog.jpa.dto;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

import static com.filip.springbootblog.jpa.constants.ValidationForEntities.MAX_LENGTH_AUTHORITY;
import static com.filip.springbootblog.jpa.constants.ValidationForEntities.MIN_LENGTH_AUTHORITY;

@Getter
@Setter
public class RoleDTO {

    private Long id;
    private boolean isLocked = false;

    @NotEmpty
    @Length(min = MIN_LENGTH_AUTHORITY, max = MAX_LENGTH_AUTHORITY)
    private String authority;

    public RoleDTO() {
    }

    @Override
    public String toString() {
        return "RoleDTO{" +
                "id=" + id +
                ", isLocked=" + isLocked +
                ", authority='" + authority + '\'' +
                '}';
    }

}
