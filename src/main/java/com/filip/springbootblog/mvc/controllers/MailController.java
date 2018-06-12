package com.filip.springbootblog.mvc.controllers;


import com.filip.springbootblog.mail.dto.MailDTO;
import com.filip.springbootblog.mail.service.interfaces.FmMailService;
import com.filip.springbootblog.mvc.components.MessageComponent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

import static com.filip.springbootblog.constants.EmailConstants.EMAIL_SENT_MESSAGE_KEY;
import static com.filip.springbootblog.constants.EmailConstants.MAIL_CONTACT_VIEW;

@Slf4j
@Controller
public class MailController {

    @Autowired
    private MessageComponent messageComponent;

    @Autowired
    private FmMailService fmMailService;

    @GetMapping("/users/contact")
    public String home(Model model) {
        model.addAttribute("mailDTO", new MailDTO());
        return MAIL_CONTACT_VIEW;
    }

    @RequestMapping(value = "/users/contact", method = RequestMethod.POST)
    public String handleContactEmail(@Valid MailDTO mailDTO, BindingResult result,
                                     RedirectAttributes attributes) {

        if (result.hasErrors()) {
            log.info("Email Errors for email from: {}", mailDTO.getFrom());
            return MAIL_CONTACT_VIEW;
        } else {
            log.debug("Sending email from: {}", mailDTO.getFrom());
            fmMailService.sendContactMail(mailDTO);
            messageComponent.addFeedbackMessage(attributes, EMAIL_SENT_MESSAGE_KEY);
            return "redirect:/users/contact";
        }
    }
}
