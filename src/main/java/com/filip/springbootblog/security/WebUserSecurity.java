package com.filip.springbootblog.security;

import com.filip.springbootblog.jpa.models.CurrentUser;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import static org.springframework.security.config.Elements.ANONYMOUS;

@Component
public class WebUserSecurity {

    public boolean canAccessAdmin(Authentication authentication) {
        if (authentication.getName().equals(ANONYMOUS)) {
            return false;
        } else {
            return ((CurrentUser) authentication.getPrincipal()).canAccessAdmin();
        }
    }
}
