package com.filip.springbootblog.mvc.controllers;

import com.filip.springbootblog.constants.AdminFeedbackMessages;
import com.filip.springbootblog.jpa.common.ISiteOption;
import com.filip.springbootblog.jpa.common.SiteOptions;
import com.filip.springbootblog.jpa.dto.RoleDTO;
import com.filip.springbootblog.jpa.dto.SiteOptionDTO;
import com.filip.springbootblog.jpa.dto.SiteOptionMapDTO;
import com.filip.springbootblog.jpa.dto.UserDTO;
import com.filip.springbootblog.jpa.dto.UserPasswordDTO;
import com.filip.springbootblog.jpa.enums.SignInProvider;
import com.filip.springbootblog.jpa.exceptions.SiteOptionNotFoundException;
import com.filip.springbootblog.jpa.models.Authority;
import com.filip.springbootblog.jpa.models.User;
import com.filip.springbootblog.jpa.models.validators.UserCreateFormValidator;
import com.filip.springbootblog.jpa.services.interfaces.IPostService;
import com.filip.springbootblog.jpa.services.interfaces.ISiteService;
import com.filip.springbootblog.jpa.services.interfaces.IUserService;
import com.filip.springbootblog.jpa.utils.UserUtils;
import com.filip.springbootblog.mvc.components.MessageComponent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

import static com.filip.springbootblog.constants.AdminFeedbackMessages.ADMIN_USER_SETPASSWORD_HEADING_KEY;
import static com.filip.springbootblog.constants.AdminFeedbackMessages.FEEDBACK_MESSAGE_KEY_ROLE_ADDED;
import static com.filip.springbootblog.constants.AdminFeedbackMessages.FEEDBACK_MESSAGE_KEY_ROLE_DELETED;
import static com.filip.springbootblog.constants.AdminFeedbackMessages.FEEDBACK_MESSAGE_KEY_ROLE_ERROR;
import static com.filip.springbootblog.constants.AdminFeedbackMessages.FEEDBACK_MESSAGE_KEY_ROLE_IS_LOCKED;
import static com.filip.springbootblog.constants.AdminFeedbackMessages.FEEDBACK_MESSAGE_KEY_ROLE_UPDATED;
import static com.filip.springbootblog.constants.AdminFeedbackMessages.FEEDBACK_MESSAGE_KEY_USER_ADDED;
import static com.filip.springbootblog.constants.AdminFeedbackMessages.FEEDBACK_MESSAGE_KEY_USER_UPDATED;
import static com.filip.springbootblog.constants.AdminFeedbackMessages.FEEDBACK_SITE_SETTINGS_UPDATED;
import static com.filip.springbootblog.constants.AdminFeedbackMessages.FEEDBACK_USER_PASSWORD_UPDATED_KEY;
import static com.filip.springbootblog.constants.AdminFeedbackMessages.GLOBAL_ERROR_PASSWORDS_DONOT_MATCH_KEY;
import static com.filip.springbootblog.constants.AdminPageViews.ADMIN_HOME_VIEW;
import static com.filip.springbootblog.constants.AdminPageViews.ADMIN_MOCKUP_VIEW;
import static com.filip.springbootblog.constants.AdminPageViews.ADMIN_ROLES_VIEW;
import static com.filip.springbootblog.constants.AdminPageViews.ADMIN_SITESETTINGS_VIEW;
import static com.filip.springbootblog.constants.AdminPageViews.ADMIN_USERFORM_VIEW;
import static com.filip.springbootblog.constants.AdminPageViews.ADMIN_USERPASSWORD_VIEW;
import static com.filip.springbootblog.constants.AdminPageViews.ADMIN_USERS_VIEW;
import static com.filip.springbootblog.constants.AdminPageViews.PARAMETER_USER_ID;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

@Slf4j
@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private ISiteService siteService;

    @Autowired
    private IUserService userService;

    @Autowired
    private SiteOptions siteOptions;

    @Autowired
    private UserCreateFormValidator userCreateFormValidator;

    @Autowired
    private IPostService postService;

    @Autowired
    private MessageComponent messageComponent;

    @InitBinder("userDTO")
    public void initUserBinder(WebDataBinder binder) {
        binder.addValidators(userCreateFormValidator);
    }

    @GetMapping(value = "")
    public String home(Model model) {
        model.addAttribute("posts", postService.getAllPosts());
        return ADMIN_HOME_VIEW;
    }

    @GetMapping(value = "/mockup")
    public String mockup(Model model) {
        return ADMIN_MOCKUP_VIEW;
    }

    // region Users

    @GetMapping(value = "/users")
    public ModelAndView userlist(Model model) {
        ModelAndView mav = new ModelAndView();
        mav.addObject("users", userService.getAllUsers());
        mav.setViewName(ADMIN_USERS_VIEW);
        return mav;
    }

    @GetMapping(value = "/users/update/{userId}")
    public ModelAndView updateUser(@PathVariable("userId") Long id) {
        return populateUserForm(id);
    }

    @GetMapping(value = "/users/new")
    public ModelAndView initAddUserForm() {
        return populateUserForm((long) -1);
    }

    @GetMapping(value = "/users/password/{userId}")
    public String setPasswordPage(@PathVariable("userId") Long userId, Model model) {
        UserPasswordDTO userPasswordDTO = new UserPasswordDTO(userId, UUID.randomUUID().toString());
        model.addAttribute("userPasswordDTO", userPasswordDTO);
        model.addAttribute("userDescription", getUserDescription(userId));
        return ADMIN_USERPASSWORD_VIEW;
    }

    @PostMapping(value = "/users/password")
    public String setPasswordPage(@Valid UserPasswordDTO userPasswordDTO, BindingResult result, Model model,
                                  RedirectAttributes attributes) {

        long userId = userPasswordDTO.getUserId();
        if (result.hasErrors()) {
            model.addAttribute("userDescription", getUserDescription(userId));
            return ADMIN_USERPASSWORD_VIEW;
        } else {
            if (!userPasswordDTO.getPassword().equals(userPasswordDTO.getRepeatedPassword())) {
                result.reject(GLOBAL_ERROR_PASSWORDS_DONOT_MATCH_KEY);
                model.addAttribute("userDescription", getUserDescription(userId));
                return ADMIN_USERPASSWORD_VIEW;
            } else {
                userService.updatePassword(userPasswordDTO);
                Optional<User> user = userService.getUserById(userPasswordDTO.getUserId());
                user.ifPresent(user1 -> messageComponent.addFeedbackMessage(attributes, FEEDBACK_USER_PASSWORD_UPDATED_KEY, user1.getFirstName(),
                        user1.getLastName()));
            }
        }
        return "redirect:/admin/users";
    }


    private ModelAndView populateUserForm(Long id) {

        ModelAndView mav = new ModelAndView();
        Optional<User> found = userService.getUserById(id);
        User user;
        if (found.isPresent()) {
            user = found.get();
            log.info("Editing User with id and username: {} {}", id, user.getUsername());
            mav.addObject("userDTO", UserUtils.userToUserDTO(user));
        } else {
            mav.addObject("userDTO", new UserDTO());
        }
        mav.addObject("authorities", userService.getRoles());
        mav.setViewName(ADMIN_USERFORM_VIEW);
        return mav;
    }

    private String getUserDescription(long userId) {
        String userDescription = null;
        Optional<User> user = userService.getUserById(userId);
        if (user.isPresent()) {
            userDescription = String.format("%s %s (%s)", user.get().getFirstName(),
                    user.get().getLastName(), user.get().getUsername());
        }
        return messageComponent.getMessage(ADMIN_USER_SETPASSWORD_HEADING_KEY, userDescription);
    }


    @PostMapping(value = "/users/update/{userId}")
    public String updateUser(@Valid UserDTO userDTO, BindingResult result,
                             RedirectAttributes attributes, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("authorities", userService.getRoles());
            return ADMIN_USERFORM_VIEW;
        } else {

            userDTO.setUpdateChildren(true);
            userService.update(userDTO);

            attributes.addAttribute(PARAMETER_USER_ID, userDTO.getUserId());
            messageComponent.addFeedbackMessage(attributes, FEEDBACK_MESSAGE_KEY_USER_UPDATED,
                    userDTO.getFirstName() + " " + userDTO.getLastName());

            return "redirect:/admin/users";
        }
    }


    @PostMapping(value = "/users/new")
    public String addUser(@Valid UserDTO userDTO,
                          BindingResult result, SessionStatus status, Model model,
                          RedirectAttributes attributes) {
        if (result.hasErrors()) {
            model.addAttribute("authorities", userService.getRoles());
            return ADMIN_USERFORM_VIEW;
        } else {
            userDTO.setSignInProvider(SignInProvider.SITE);
            User added = userService.create(userDTO);
            log.info("Added user with information: {}", added);
            status.setComplete();

            messageComponent.addFeedbackMessage(attributes, FEEDBACK_MESSAGE_KEY_USER_ADDED, added.getFirstName(),
                    added.getLastName());

            return "redirect:/admin/users";
        }
    }

    @PostMapping("/roles/new")
    public String addUser(@Valid RoleDTO roleDTO,
                          BindingResult result,
                          SessionStatus status,
                          RedirectAttributes attributes) {
        if (result.hasErrors()) {
            return ADMIN_ROLES_VIEW;
        } else {
            Authority authority = userService.createAuthority(roleDTO);
            log.info("Role Added: {}", authority);
            status.setComplete();

            messageComponent.addFeedbackMessage(attributes, FEEDBACK_MESSAGE_KEY_ROLE_ADDED, authority.getAuthority());
            return "redirect:/admin/roles";
        }
    }


    @PostMapping(value = "/roles/update/{Id}")
    public String updateRole(@Valid @ModelAttribute(value = "authority") RoleDTO roleDTO, BindingResult result,
                             RedirectAttributes attributes, Model model) {
        if (result.hasErrors()) {
            messageComponent.addFeedbackMessage(attributes, FEEDBACK_MESSAGE_KEY_ROLE_ERROR);
            return "redirect:/admin/roles";
        } else {
            Authority authority = userService.getAuthorityById(roleDTO.getId());
            if (authority.isLocked()) {
                messageComponent.addFeedbackMessage(attributes, FEEDBACK_MESSAGE_KEY_ROLE_IS_LOCKED, roleDTO.getAuthority().toUpperCase());
                return "redirect:/admin/roles";
            } else {
                userService.updateAuthority(roleDTO);
                messageComponent.addFeedbackMessage(attributes, FEEDBACK_MESSAGE_KEY_ROLE_UPDATED, roleDTO.getAuthority());
                return "redirect:/admin/roles";
            }
        }
    }

    @PostMapping(value = "/roles/update/{id}", params = {"deleteRole"})
    public String deleteRole(@Valid @ModelAttribute(value = "authority") RoleDTO roleDTO, BindingResult result, RedirectAttributes attributes, Model model) {
        if (result.hasErrors()) {
            messageComponent.addFeedbackMessage(attributes, FEEDBACK_MESSAGE_KEY_ROLE_ERROR);
            return "redirect:/admin/roles";
        } else {
            Authority authority = userService.getAuthorityById(roleDTO.getId());
            if (authority.isLocked()) {
                messageComponent.addFeedbackMessage(attributes, FEEDBACK_MESSAGE_KEY_ROLE_IS_LOCKED, roleDTO.getAuthority());

            } else {
                Collection<User> users = userService.getUsersByAuthorityId(roleDTO.getId());
                userService.deleteAuthority(authority, users);
                messageComponent.addFeedbackMessage(attributes, FEEDBACK_MESSAGE_KEY_ROLE_DELETED, roleDTO.getAuthority(), users.size());
            }
            return "redirect:/admin/roles";
        }
    }

    @GetMapping("/roles")
    public ModelAndView roleList(Model model) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("roles", userService.getRoles());
        modelAndView.addObject("newRole", new Authority());
        modelAndView.setViewName(ADMIN_ROLES_VIEW);
        return modelAndView;
    }

    @GetMapping("/site/settings")
    public ModelAndView siteSettings(Model model) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("siteOptionMapDTO", getGeneralSiteSettings());
        modelAndView.setViewName(ADMIN_SITESETTINGS_VIEW);
        return modelAndView;
    }

    @PostMapping("/site/settings")
    public String siteSettings(@Valid SiteOptionMapDTO siteOptionMapDTO,
                               BindingResult result,
                               RedirectAttributes attributes) throws SiteOptionNotFoundException {

        if (hasSiteSettingsErrors(result)) {
            return ADMIN_SITESETTINGS_VIEW;
        } else {
            updateGeneralSiteSettings(siteOptionMapDTO);
            messageComponent.addFeedbackMessage(attributes, FEEDBACK_SITE_SETTINGS_UPDATED);
            return "redirect:/admin/site/settings";
        }
    }

// region Utility Methods

    private SiteOptionMapDTO getGeneralSiteSettings() {
        return SiteOptionMapDTO.withGeneralSettings(
                siteOptions.getSiteName(),
                siteOptions.getSiteDescription(),
                siteOptions.getAddGoogleAnalytics(),
                siteOptions.getGoogleAnalyticsTrackingId(),
                siteOptions.getUserRegistration())
                .build();
    }

    private void updateGeneralSiteSettings(SiteOptionMapDTO siteOptionMapDTO)
            throws SiteOptionNotFoundException {

        siteService.update(SiteOptionDTO.with(ISiteOption.SITE_NAME, siteOptionMapDTO.getSiteName()).build());
        siteService.update(SiteOptionDTO.with(ISiteOption.SITE_DESCRIPTION, siteOptionMapDTO.getSiteDescription()).build());
        siteService.update(SiteOptionDTO.with(ISiteOption.ADD_GOOGLE_ANALYTICS, siteOptionMapDTO.getAddGoogleAnalytics()).build());
        siteService.update(SiteOptionDTO.with(ISiteOption.GOOGLE_ANALYTICS_TRACKING_ID, siteOptionMapDTO.getGoogleAnalyticsTrackingId()).build());
        siteService.update(SiteOptionDTO.with(ISiteOption.USER_REGISTRATION, siteOptionMapDTO.getUserRegistration()).build());

    }

    private Boolean hasSiteSettingsErrors(BindingResult result) {
        for (FieldError error : result.getFieldErrors()) {
            if (!error.getField().equals("integerProperty"))
                return true;
        }
        return false;
    }
}
