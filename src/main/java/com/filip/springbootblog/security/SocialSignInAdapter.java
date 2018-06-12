package com.filip.springbootblog.security;

import com.filip.springbootblog.jpa.models.User;
import com.filip.springbootblog.jpa.repositories.UserRepository;
import com.filip.springbootblog.jpa.services.interfaces.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionData;
import org.springframework.social.connect.web.SignInAdapter;
import org.springframework.web.context.request.NativeWebRequest;

import javax.inject.Inject;

public class SocialSignInAdapter implements SignInAdapter {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private IUserService userService;

    @Inject
    private RequestCache requestCache;

    @Override
    public String signIn(String localUserId, Connection<?> connection, NativeWebRequest request) {
        User user = userRepository.findByUsername(localUserId);
        ConnectionData connectionData = connection.createData();
        SignInUtils.authorizeUser(user);
        SignInUtils.setUserConnection(request, connectionData);
        return null;
    }
}
