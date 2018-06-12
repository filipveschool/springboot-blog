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

import static com.filip.springbootblog.constants.ValidationForEntities.MAX_LENGTH_EMAIL_ADDRESS;
import static com.filip.springbootblog.constants.ValidationForEntities.MAX_LENGTH_FIRST_NAME;
import static com.filip.springbootblog.constants.ValidationForEntities.MAX_LENGTH_LAST_NAME;
import static com.filip.springbootblog.constants.ValidationForEntities.MAX_LENGTH_USERNAME;
import static com.filip.springbootblog.constants.ValidationForEntities.MIN_LENGTH_FIRST_NAME;
import static com.filip.springbootblog.constants.ValidationForEntities.MIN_LENGTH_LAST_NAME;
import static com.filip.springbootblog.constants.ValidationForEntities.MIN_LENGTH_USERNAME;

@Getter
@Setter
public class SocialUserDTO {

    @Length(min = MIN_LENGTH_USERNAME, max = MAX_LENGTH_USERNAME)
    private String username = "";

    @Basic
    @ExtendedEmailValidator
    @Length(max = MAX_LENGTH_EMAIL_ADDRESS)
    private String email = "";

    private String password = "";

    private SignInProvider signInProvider;

    @NotEmpty
    @Length(min = MIN_LENGTH_FIRST_NAME, max = MAX_LENGTH_FIRST_NAME)
    private String firstName = "";

    @NotEmpty
    @Length(min = MIN_LENGTH_LAST_NAME, max = MAX_LENGTH_LAST_NAME)
    private String lastName = "";

    private Collection<Authority> authorities;

    public SocialUserDTO() {

    }


    @Override
    public String toString() {
        return "SocialUserDTO{" +
                ", username='" + username + '\'' +
                ", firstName='" + firstName + '\'' +
                "lastName='" + lastName + '\'' +
                "signInProvider='" + signInProvider + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
