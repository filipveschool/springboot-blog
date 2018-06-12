package com.filip.springbootblog.mvc.controllers;

import com.filip.springbootblog.jpa.constants.FeedbackMessages;
import com.filip.springbootblog.jpa.dto.ForgotEmailDTO;
import com.filip.springbootblog.jpa.dto.UserPasswordDTO;
import com.filip.springbootblog.jpa.enums.ResetPasswordResult;
import com.filip.springbootblog.jpa.models.CurrentUser;
import com.filip.springbootblog.jpa.models.User;
import com.filip.springbootblog.jpa.models.UserToken;
import com.filip.springbootblog.jpa.models.validators.UserPasswordValidator;
import com.filip.springbootblog.jpa.services.interfaces.IUserService;
import com.filip.springbootblog.jpa.utils.SharedUtils;
import com.filip.springbootblog.mvc.components.MessageComponent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.util.Optional;
import java.util.UUID;

import static com.filip.springbootblog.jpa.constants.PageViews.USER_CHANGEPASSWORD_VIEW;
import static com.filip.springbootblog.jpa.constants.PageViews.USER_FORGOTPASSWORD_VIEW;

@Slf4j
@Controller
public class UserPasswordController {

    @Autowired
    private IUserService userService;

    @Autowired
    private UserPasswordValidator userPasswordValidator;

    @Autowired
    private MessageComponent messageComponent;

    @PostMapping(value = "/users/forgotpassword")
    public String sendForgotEmail(@Valid ForgotEmailDTO forgotEmailDTO,
                                  BindingResult result, Model model) {
        if (result.hasErrors()) {
            return USER_FORGOTPASSWORD_VIEW;
        } else {
            Optional<User> user = userService.getUserByEmail(forgotEmailDTO.getEmail());
            if (!user.isPresent()) {
                result.reject("global.error.eail.does.not.exist");
            } else {
                model.addAttribute(FeedbackMessages.FLASH_MESSAGE_KEY_FEEDBACK,
                        messageComponent.getMessage(FeedbackMessages.FEEDBACK_PASSWORD_EMAIL_SENT));
                model.addAttribute("forgotEmailDTO", new ForgotEmailDTO());

                UserToken userToken = userService.createUserToken(user.get());
                fmMailService.sendResetPasswordMail(user.get(), userToken.getToken());
            }
        }

        return USER_FORGOTPASSWORD_VIEW;
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("/users/resetpassword")
    public String changePassword(CurrentUser currentUser, Model model) {
        UserPasswordDTO userPasswordDTO = new UserPasswordDTO(currentUser.getId(), UUID.randomUUID().toString());
        model.addAttribute("userPasswordDTO", userPasswordDTO);
        return USER_CHANGEPASSWORD_VIEW;
    }

    @GetMapping("/users/resetpassword/{token}")
    public String changePasswordFromEmail(@PathVariable String token, Model model) {
        UserPasswordDTO userPasswordDTO = new UserPasswordDTO(SharedUtils.randomNegativeId(), token);
        model.addAttribute("userPasswordDTO", userPasswordDTO);
        return USER_CHANGEPASSWORD_VIEW;
    }

    @PostMapping("/users/resetpassword")
    public ModelAndView changePassword(@Valid @ModelAttribute("userPasswordDTO") UserPasswordDTO userPasswordDTO,
                                       BindingResult result) {
        ModelAndView modelAndView = new ModelAndView();
        if (!result.hasErrors()) {
            ResetPasswordResult resetPasswordResult = userService.updatePassword(userPasswordDTO);

            switch (resetPasswordResult) {
                case ERROR:
                    result.reject("global.error.password.reset");
                    break;
                case FORGOT_SUCCESSFUL:
                    modelAndView.addObject(FeedbackMessages.FLASH_MESSAGE_KEY_FEEDBACK,
                            messageComponent.getMessage(FeedbackMessages.FEEDBACK_PASSWORD_LOGIN));
                    break;
                case LOGGEDIN_SUCCESSFUL:
                    modelAndView.addObject(FeedbackMessages.FLASH_MESSAGE_KEY_FEEDBACK,
                            messageComponent.getMessage(FeedbackMessages.FEEDBACK_PASSWORD_RESET));
                    break;
            }
        }

        modelAndView.addObject("userPasswordDTO", userPasswordDTO);
        modelAndView.setViewName(USER_CHANGEPASSWORD_VIEW);
        return modelAndView;
    }

}
