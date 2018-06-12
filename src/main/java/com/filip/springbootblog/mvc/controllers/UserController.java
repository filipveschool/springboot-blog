package com.filip.springbootblog.mvc.controllers;

import com.filip.springbootblog.jpa.dto.ProfileImageDTO;
import com.filip.springbootblog.jpa.dto.SocialUserDTO;
import com.filip.springbootblog.jpa.dto.UserDTO;
import com.filip.springbootblog.jpa.enums.SignInProvider;
import com.filip.springbootblog.jpa.models.Authority;
import com.filip.springbootblog.jpa.models.User;
import com.filip.springbootblog.jpa.models.UserConnection;
import com.filip.springbootblog.jpa.models.validators.SocialUserFormValidator;
import com.filip.springbootblog.jpa.models.validators.UserCreateFormValidator;
import com.filip.springbootblog.jpa.services.interfaces.IUserService;
import com.filip.springbootblog.mvc.components.ImageComponent;
import com.filip.springbootblog.mvc.components.MessageComponent;
import com.filip.springbootblog.security.SignInUtils;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionData;
import org.springframework.social.connect.ConnectionKey;
import org.springframework.social.connect.UserProfile;
import org.springframework.social.connect.web.ProviderSignInUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

import static com.filip.springbootblog.constants.FeedbackMessages.MESSAGE_KEY_SOCIAL_SIGNUP;
import static com.filip.springbootblog.constants.GeneralConstants.ERROR_PAGE_MESSAGE_ATTRIBUTE;
import static com.filip.springbootblog.constants.GeneralConstants.ERROR_PAGE_TITLE_ATTRIBUTE;
import static com.filip.springbootblog.constants.GeneralConstants.USER_VERIFICATION_EMAIL_SENT;
import static com.filip.springbootblog.constants.GeneralConstants.USER_VERIFICATION_ERROR_MESSAGE;
import static com.filip.springbootblog.constants.GeneralConstants.USER_VERIFICATION_ERROR_TITLE;
import static com.filip.springbootblog.constants.PageViews.ERROR_CUSTOM_VIEW;
import static com.filip.springbootblog.constants.PageViews.REDIRECT_HOME_VIEW;
import static com.filip.springbootblog.constants.PageViews.SIGNUP_VIEW;
import static com.filip.springbootblog.constants.PageViews.USER_PROFILE_VIEW;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Slf4j
@Controller
public class UserController {

    public static final String MODEL_ATTRIBUTE_CURRENTUSER = "currentUser";
    private static final String MODEL_ATTRIBUTE_SOCIALUSER = "socialUserDTO";

    @Autowired
    private IUserService userService;

    @Autowired
    private UserCreateFormValidator userCreateFormValidator;

    @Autowired
    private SocialUserFormValidator socialUserFormValidator;

    private ProviderSignInUtils providerSignInUtils;

    @Autowired
    private ImageComponent imageComponent;

    @Autowired
    private MessageComponent messageComponent;



    // region validators

    @InitBinder("userDTO")
    public void initUserBinder(WebDataBinder binder) {
        binder.addValidators(userCreateFormValidator);
    }

    @InitBinder("socialUserDTO")
    public void initSocialUserBinder(WebDataBinder binder) {
        binder.addValidators(socialUserFormValidator);
    }

    @RequestMapping(value = "/users/reverify/{username}", method = RequestMethod.GET)
    public String resendUserVerification(@PathVariable String username, RedirectAttributes redirectAttributes) throws UsernameNotFoundException {
        User user = userService.getUserByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException(username + " not found!");
        } else {
            redirectAttributes.addFlashAttribute("statusMessage", messageComponent.getMessage(USER_VERIFICATION_EMAIL_SENT, user.getEmail()));
            return "redirect:/signin?message";
        }
    }

    @GetMapping(value = "/users/verify/{userkey}")
    public String verifyUser(@PathVariable("userkey") String userkey, Model model,
                             RedirectAttributes redirectAttributes) {
        Optional<User> user = userService.getByUserKey(userkey);
        String viewName = ERROR_CUSTOM_VIEW;
        if (!user.isPresent()) {
            model.addAttribute(ERROR_PAGE_TITLE_ATTRIBUTE,
                    messageComponent.getMessage(USER_VERIFICATION_ERROR_TITLE));
            model.addAttribute(ERROR_PAGE_MESSAGE_ATTRIBUTE,
                    messageComponent.getMessage(USER_VERIFICATION_ERROR_MESSAGE));
        } else {
            userService.enableAndApproveUser(user.get());
            redirectAttributes.addFlashAttribute("emailVerifiedWelcomeMessage", true);
            viewName = REDIRECT_HOME_VIEW;
        }
        return viewName;
    }

    // region Social User Signup

    @RequestMapping(value = "/signup", method = RequestMethod.GET)
    public String signupForm(@ModelAttribute SocialUserDTO socialUserDTO, WebRequest request, Model model) {


        if (request.getUserPrincipal() != null)
            return "redirect:/";
        else {
            Connection<?> connection = providerSignInUtils.getConnectionFromSession(request);
            request.setAttribute("connectionSubheader",
                    messageComponent.parameterizedMessage(MESSAGE_KEY_SOCIAL_SIGNUP,
                            StringUtils.capitalize(connection.getKey().getProviderId())),
                    RequestAttributes.SCOPE_REQUEST);

            socialUserDTO = createSocialUserDTO(request, connection);

            ConnectionData connectionData = connection.createData();
            SignInUtils.setUserConnection(request, connectionData);

            model.addAttribute(MODEL_ATTRIBUTE_SOCIALUSER, socialUserDTO);
            return SIGNUP_VIEW;
        }
    }


    @PostMapping(value = "/signup")
    public String signup(@Valid @ModelAttribute("socialUserDTO") SocialUserDTO socialUserDTO,
                         BindingResult result,
                         WebRequest request, RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            return SIGNUP_VIEW;
        }

        UserDTO userDTO = createUserDTO(socialUserDTO);
        User user = userService.create(userDTO);

        providerSignInUtils.doPostSignUp(userDTO.getUsername(), request);
        UserConnection userConnection =
                userService.getUserConnectionByUserId(userDTO.getUsername());
        if (userConnection.getImageUrl() != null) {
            try {
                imageComponent.processProfileImage(userConnection.getImageUrl(), user.getUserKey());
                userService.updateHasAvatar(user.getId(), true);
            } catch (IOException e) {
                log.error("ImageUrl IOException for username: {0}", user.getUsername());
            }
        }
        SignInUtils.authorizeUser(user);

        redirectAttributes.addFlashAttribute("connectionWelcomeMessage", true);
        return "redirect:/";
    }


    private UserDTO createUserDTO(SocialUserDTO socialUserDTO) {
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername(socialUserDTO.getUsername().toLowerCase());
        userDTO.setFirstName(socialUserDTO.getFirstName());
        userDTO.setLastName(socialUserDTO.getLastName());
        userDTO.setEmail(socialUserDTO.getEmail());
        userDTO.setSignInProvider(socialUserDTO.getSignInProvider());
        userDTO.setPassword(UUID.randomUUID().toString());
        userDTO.setAuthorities(Lists.newArrayList(new Authority("ROLE_USER")));
        return userDTO;
    }

    private SocialUserDTO createSocialUserDTO(WebRequest request, Connection<?> connection) {
        SocialUserDTO dto = new SocialUserDTO();

        if (connection != null) {
            UserProfile socialMediaProfile = connection.fetchUserProfile();
            dto.setEmail(socialMediaProfile.getEmail());
            dto.setFirstName(socialMediaProfile.getFirstName());
            dto.setLastName(socialMediaProfile.getLastName());

            ConnectionKey providerKey = connection.getKey();
            dto.setSignInProvider(SignInProvider.valueOf(providerKey.getProviderId().toUpperCase()));

        }

        return dto;
    }

    // endregion


    // region User Profile and Account Services

//    @PreAuthorize("#username == authentication.name")
//    @RequestMapping(value = "/{username}", method = GET)
//    public String profilePage(@PathVariable("username") String username,
//                              Model model, WebRequest request)
//            throws UsernameNotFoundException {
//
//        logger.info("Showing user page for user: {}", username);
//        ProfileImageDTO profileImageDTO = new ProfileImageDTO();
//        model.addAttribute("profileImageDTO", profileImageDTO);
//
//        return USER_PROFILE_VIEW;
//    }

    @PreAuthorize("#username == authentication.name")
    @GetMapping(value = "/user/{username}")
    public String userProfilePage(@PathVariable("username") String username,
                                  Model model) throws UsernameNotFoundException {
        log.info("Showing user page for user: {}", username);
        ProfileImageDTO profileImageDTO = new ProfileImageDTO();
        model.addAttribute("profileImageDTO", profileImageDTO);

        return USER_PROFILE_VIEW;
    }

    // endregion

}
