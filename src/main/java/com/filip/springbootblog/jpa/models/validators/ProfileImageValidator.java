package com.filip.springbootblog.jpa.models.validators;

import com.filip.springbootblog.jpa.dto.ProfileImageDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import javax.imageio.ImageIO;
import java.io.IOException;
import java.io.InputStream;

@Slf4j
@Component
public class ProfileImageValidator implements Validator {

    @Value("${profile.image.upload.size}")
    private Long imageMaxSize;

    @Override
    public boolean supports(Class<?> aClass) {
        return aClass.equals(ProfileImageDTO.class);
    }

    @Override
    public void validate(Object target, Errors errors) {
        log.debug("Validating {}", target.toString());
        ProfileImageDTO profileImage = (ProfileImageDTO) target;
        validateFileType(errors, profileImage);
        validateForMinMaxFileSize(errors, profileImage);
    }

    private void validateForMinMaxFileSize(Errors errors, ProfileImageDTO profileImage) {
        if (profileImage.getFile().getSize() == 0) {
            errors.reject("file.empty");
        }
        if (profileImage.getFile().getSize() > imageMaxSize) {
            errors.reject("file.too.large");
        }
    }

    private void validateFileType(Errors errors, ProfileImageDTO profileImage) {

        try (InputStream input = profileImage.getFile().getInputStream()) {
            try {
                ImageIO.read(input);
            } catch (Exception e) {
                errors.reject("file.not.an.image");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
