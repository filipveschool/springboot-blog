package com.filip.springbootblog.jpa.models.validators;

import com.filip.springbootblog.jpa.dto.UserPasswordDTO;
import com.filip.springbootblog.jpa.models.User;
import com.filip.springbootblog.jpa.services.interfaces.IUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.Optional;

@Slf4j
@Component
public class UserPasswordValidator implements Validator {

    @Autowired
    private IUserService userService;


    @Override
    public boolean supports(Class<?> aClass) {
        return aClass.equals(UserPasswordDTO.class);
    }

    @Override
    public void validate(Object target, Errors errors) {
        log.debug("Validating {}", target);
        UserPasswordDTO form = (UserPasswordDTO) target;
        validatePasswords(errors, form);
        preventDemoUserUpdate(errors, form);
    }

    private void preventDemoUserUpdate(Errors errors, UserPasswordDTO form) {
        Optional<User> user = userService.getUserById(form.getUserId());
        if (user.isPresent()) {
            if (user.get().getUsername().toLowerCase().equals("user")) {
                errors.reject("global.error.password.demo.user");
            }
        }
    }

    private void validatePasswords(Errors errors, UserPasswordDTO form) {
        if (!form.getPassword().equals(form.getRepeatedPassword())) {
            errors.reject("password.no_match", "Passwords do not match");
        }
    }
}
