package com.filip.springbootblog.jpa.dto;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import java.io.Serializable;

import static com.filip.springbootblog.jpa.constants.ValidationForEntities.MIN_LENGTH_PASSWORD;

@Getter
@Setter
public class UserPasswordDTO implements Serializable {


    private static final long serialVersionUID = 5005130123084249387L;

    @Length(min = MIN_LENGTH_PASSWORD)
    private String password;

    @Length(min = MIN_LENGTH_PASSWORD)
    private String repeatedPassword;

    private String verificationToken;
    private long userId;

    public UserPasswordDTO() {
    }

    public UserPasswordDTO(long userId, String verificationToken) {
        this.verificationToken = verificationToken;
        this.userId = userId;
    }

    public UserPasswordDTO(long userId, String verificationToken, String password, String repeatedPassword) {
        this.password = password;
        this.repeatedPassword = repeatedPassword;
        this.verificationToken = verificationToken;
        this.userId = userId;
    }

}
