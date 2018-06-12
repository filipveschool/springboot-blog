package com.filip.springbootblog.jpa.dto;

import com.filip.springbootblog.jpa.enums.SignInProvider;
import com.filip.springbootblog.jpa.models.Authority;
import com.filip.springbootblog.jpa.models.validators.ExtendedEmailValidator;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.Basic;
import java.util.Collection;

import static com.filip.springbootblog.jpa.constants.ValidationForEntities.MAX_LENGTH_EMAIL_ADDRESS;
import static com.filip.springbootblog.jpa.constants.ValidationForEntities.MAX_LENGTH_FIRST_NAME;
import static com.filip.springbootblog.jpa.constants.ValidationForEntities.MAX_LENGTH_LAST_NAME;
import static com.filip.springbootblog.jpa.constants.ValidationForEntities.MAX_LENGTH_PASSWORD;
import static com.filip.springbootblog.jpa.constants.ValidationForEntities.MAX_LENGTH_USERNAME;
import static com.filip.springbootblog.jpa.constants.ValidationForEntities.MIN_LENGTH_FIRST_NAME;
import static com.filip.springbootblog.jpa.constants.ValidationForEntities.MIN_LENGTH_LAST_NAME;
import static com.filip.springbootblog.jpa.constants.ValidationForEntities.MIN_LENGTH_PASSWORD;
import static com.filip.springbootblog.jpa.constants.ValidationForEntities.MIN_LENGTH_USERNAME;

@Getter
@Setter
public class UserDTO {

    private Long userId;
    private boolean updateChildren = true;
    private boolean isEnabled = true;

    @Length(min = MIN_LENGTH_USERNAME, max = MAX_LENGTH_USERNAME)
    private String username = "";

    @Basic
    @ExtendedEmailValidator
    @Length(max = MAX_LENGTH_EMAIL_ADDRESS)
    private String email = "";

    @Length(min = MIN_LENGTH_PASSWORD, max = MAX_LENGTH_PASSWORD)
    private String password = "";

    @NotEmpty
    @Length(min = MIN_LENGTH_FIRST_NAME, max = MAX_LENGTH_FIRST_NAME)
    private String firstName = "";

    @NotEmpty
    @Length(min = MIN_LENGTH_LAST_NAME, max = MAX_LENGTH_LAST_NAME)
    private String lastName = "";

    private boolean hasAvatar;
    private String userKey;

    private SignInProvider signInProvider;

    private String repeatedPassword = "";

    private Collection<Authority> authorities;

    public UserDTO() {

    }

    public boolean isNew() {
        return (this.userId == null);
    }


    public Collection<Authority> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(Collection<Authority> authorities) {
        this.authorities = authorities;
    }


    @Override
    public String toString() {
        return "UserCreateForm{" +
                ", username=" + username +
                ", firstName=" + firstName +
                ", lastName=" + lastName +
                ", email=" + email +
                '}';
    }
}
