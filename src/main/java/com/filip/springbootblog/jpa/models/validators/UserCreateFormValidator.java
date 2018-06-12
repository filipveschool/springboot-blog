package com.filip.springbootblog.jpa.models.validators;

import com.filip.springbootblog.jpa.dto.UserDTO;
import com.filip.springbootblog.jpa.models.User;
import com.filip.springbootblog.jpa.services.interfaces.IAccessService;
import com.filip.springbootblog.jpa.services.interfaces.IUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Slf4j
@Component
public class UserCreateFormValidator implements Validator {

    @Autowired
    private IAccessService accessService;

    @Autowired
    private IUserService userService;

    @Override
    public boolean supports(Class<?> aClass) {
        return aClass.equals(UserDTO.class);
    }

    @Override
    public void validate(Object target, Errors errors) {
        log.debug("Validating {}", target);
        UserDTO form = (UserDTO) target;
        if (form.isNew()) {
            validatePasswords(errors, form);
            validateEmail(errors, form);
            validateDomain(errors, form);
            validateUsername(errors, form);
        } else {
            validateUsername(errors, form, form.getUserId());
        }
    }

    private void validatePasswords(Errors errors, UserDTO form) {
        if (!form.getPassword().equals(form.getRepeatedPassword())) {
            errors.reject("password.no_match", "Passwords do not match");
        }
    }

    private void validateEmail(Errors errors, UserDTO form) {
        if (userService.getUserByEmail(form.getEmail()).isPresent()) {
            errors.reject("email.exists", "User with this email already exists");
        }
    }

    private void validateDomain(Errors errors, UserDTO form) {
        if (!accessService.isEmailApproved(form.getEmail())) {
            errors.reject("domain.not.approved",
                    "The email address domain is not currently supported");
        }
    }

    private void validateUsername(Errors errors, UserDTO form) {
        if (userService.getUserByUsername(form.getUsername()) != null) {
            errors.reject("user.exists", "User with this username already exists");
        }
    }

    private void validateUsername(Errors errors, UserDTO form, long userId) {
        User user = userService.getUserByUsername(form.getUsername());
        if (user != null) {
            if (user.getId() != userId) {
                errors.reject("user.exists", "User with this username already exists");
            }
        }
    }
}

