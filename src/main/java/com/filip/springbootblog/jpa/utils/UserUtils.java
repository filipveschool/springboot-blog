package com.filip.springbootblog.jpa.utils;


import com.filip.springbootblog.jpa.dto.UserDTO;
import com.filip.springbootblog.jpa.models.Authority;
import com.filip.springbootblog.jpa.models.User;
import com.filip.springbootblog.jpa.models.UserData;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;

public class UserUtils {

    public static UserDTO userToUserDTO(User user) {
        UserDTO userDTO = new UserDTO();
        userDTO.setUserId(user.getId());
        userDTO.setFirstName(user.getFirstName());
        userDTO.setLastName(user.getLastName());
        userDTO.setUsername(user.getUsername());
        userDTO.setPassword(user.getPassword());
        userDTO.setRepeatedPassword(user.getPassword());
        userDTO.setEmail(user.getEmail());
        userDTO.setSignInProvider(user.getSignInProvider());
        userDTO.setAuthorities((Collection<Authority>) user.getAuthorities());
        userDTO.setHasAvatar(user.isHasAvatar());
        userDTO.setUserKey(user.getUserKey());
        return userDTO;
    }

    public static String bcryptedPassword(String rawPassword) {
        return new BCryptPasswordEncoder().encode(rawPassword);
    }

    public static UserData newRegisteredUserData(User user) {
        Date now = Calendar.getInstance().getTime();
        UserData userData = new UserData();
        userData.setUserId(user.getId());
        userData.setCreatedDatetime(now);
        userData.setLastloginDatetime(now);
        userData.setLoginAttempts(0);
        return userData;
    }
}
