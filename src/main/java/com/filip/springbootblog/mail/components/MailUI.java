package com.filip.springbootblog.mail.components;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Locale;

@Component
public class MailUI {

    @Resource(name = "mailMessageSource")
    private MessageSource messageSource;

    public String getMessage(String code, Object... params) {
        Locale current = LocaleContextHolder.getLocale();
        return messageSource.getMessage(code, params, current);
    }
}
