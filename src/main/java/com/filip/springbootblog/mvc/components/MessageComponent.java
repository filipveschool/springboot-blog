package com.filip.springbootblog.mvc.components;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.annotation.Resource;
import java.util.Locale;

import static com.filip.springbootblog.jpa.constants.FeedbackMessages.FLASH_MESSAGE_KEY_FEEDBACK;

@Slf4j
@Component
public class MessageComponent {

    @Resource
    private MessageSource messageSource;

    public String getMessage(String code, Object... params) {
        Locale current = LocaleContextHolder.getLocale();
        return messageSource.getMessage(code, params, current);
    }

    public void addFeedbackMessage(RedirectAttributes model, String code, Object... params) {
        String localizedFeedbackMessage = getMessage(code, params);
        model.addFlashAttribute(FLASH_MESSAGE_KEY_FEEDBACK, localizedFeedbackMessage);
    }

    public String parameterizedMessage(String code, Object... params) {
        String localizedMessage = getMessage(code, params);
        return localizedMessage;
    }
}
