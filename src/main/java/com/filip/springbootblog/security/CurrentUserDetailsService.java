package com.filip.springbootblog.security;

import com.filip.springbootblog.jpa.models.CurrentUser;
import com.filip.springbootblog.jpa.models.User;
import com.filip.springbootblog.jpa.repositories.UserRepository;
import org.omg.CORBA.Current;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CurrentUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    private static final String USER_IS_DISABLED = "User is disabled";

    @Override
    public CurrentUser loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);
        if (!user.isEnabled()) {
            throw new DisabledException(USER_IS_DISABLED);
        }
        return new CurrentUser(user);
    }
}
