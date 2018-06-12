package com.filip.springbootblog.security;

import com.filip.springbootblog.jpa.models.CurrentUser;
import com.filip.springbootblog.jpa.models.User;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.social.connect.ConnectionData;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.WebRequest;
import static com.filip.springbootblog.constants.GeneralConstants.SESSION_USER_CONNECTION;

public class SignInUtils {

    public static void authorizeUser(User user) {

        CurrentUser currentUser = new CurrentUser(user);
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(currentUser, user.getPassword(), user.getAuthorities()));

    }

    public static void setUserConnection(WebRequest request, ConnectionData connectionData) {
        String attribute = SESSION_USER_CONNECTION;
        request.setAttribute(attribute, connectionData, RequestAttributes.SCOPE_SESSION);
    }
}
