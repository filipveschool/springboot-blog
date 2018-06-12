package com.filip.springbootblog.mail.service.interfaces;

import com.filip.springbootblog.jpa.models.User;
import com.filip.springbootblog.mail.dto.MailDTO;

public interface FmMailService {

    void sendResetPasswordMail(User user, String token);

    void sendContactMail(MailDTO mailDTO);

    void sendUserVerificationMail(User user);
}
