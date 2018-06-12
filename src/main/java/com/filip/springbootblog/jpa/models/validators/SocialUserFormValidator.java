package com.filip.springbootblog.jpa.models.validators;

import com.filip.springbootblog.jpa.dto.SocialUserDTO;
import com.filip.springbootblog.jpa.services.interfaces.IUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Slf4j
@Component
public class SocialUserFormValidator implements Validator {

    @Autowired
    private IUserService userService;

    @Override
    public boolean supports(Class<?> aClass) {
        return aClass.equals(SocialUserDTO.class);
    }

    @Override
    public void validate(Object target, Errors errors) {
        log.debug("Validating {}", target);
        SocialUserDTO form = (SocialUserDTO) target;
        validateEmail(errors, form);
        validateUsername(errors, form);
    }

    private void validateEmail(Errors errors, SocialUserDTO form) {
        if (userService.getUserByEmail(form.getEmail()).isPresent()) {
            errors.reject("email.exists", "User with this email already exists");
        }
    }

    private void validateUsername(Errors errors, SocialUserDTO form) {
        if (userService.getUserByUsername(form.getUsername()) != null) {
            errors.reject("user.exists", "User with this username already exists");
        }
    }
}
