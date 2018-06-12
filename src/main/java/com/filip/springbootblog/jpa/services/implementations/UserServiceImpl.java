package com.filip.springbootblog.jpa.services.implementations;

import com.filip.springbootblog.jpa.dto.RoleDTO;
import com.filip.springbootblog.jpa.dto.UserDTO;
import com.filip.springbootblog.jpa.dto.UserPasswordDTO;
import com.filip.springbootblog.jpa.enums.ResetPasswordResult;
import com.filip.springbootblog.jpa.enums.Role;
import com.filip.springbootblog.jpa.models.Authority;
import com.filip.springbootblog.jpa.models.CurrentUser;
import com.filip.springbootblog.jpa.models.User;
import com.filip.springbootblog.jpa.models.UserConnection;
import com.filip.springbootblog.jpa.models.UserData;
import com.filip.springbootblog.jpa.models.UserToken;
import com.filip.springbootblog.jpa.repositories.AuthorityRepository;
import com.filip.springbootblog.jpa.repositories.UserConnectionRepository;
import com.filip.springbootblog.jpa.repositories.UserDataRepository;
import com.filip.springbootblog.jpa.repositories.UserRepository;
import com.filip.springbootblog.jpa.repositories.UserTokenRepository;
import com.filip.springbootblog.jpa.services.interfaces.IUserService;
import com.filip.springbootblog.jpa.utils.UserUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service("userService")
@Transactional
public class UserServiceImpl implements IUserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserDataRepository userDataRepository;

    @Autowired
    private UserConnectionRepository userConnectionRepository;

    @Autowired
    private UserTokenRepository userTokenRepository;

    @Autowired
    private AuthorityRepository authorityRepository;


    @Override
    public Optional<User> getUserById(Long id) {
        log.debug("Getting user={}", id);
        return userRepository.findById(id);
    }

    @Override
    public Optional<User> getUserByEmail(String email) {
        log.debug("Getting user by email={}", email);
        return userRepository.findByEmail(email);
    }

    @Override
    public Optional<User> getByUserKey(String userKey) {
        log.debug("Getting user by userkey={}", userKey);
        return userRepository.findByUserKey(userKey);
    }

    @Override
    public Collection<User> getAllUsers() {
        log.debug("Getting all users");
        return userRepository.findAll();
    }

    @Override
    public User create(UserDTO userDTO) {
        User user = new User();

        user.setUsername(userDTO.getUsername());
        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        user.setEmail(userDTO.getEmail());
        user.setPassword(new BCryptPasswordEncoder().encode(userDTO.getPassword()));
        user.setUserKey(RandomStringUtils.randomAlphanumeric(16));
        user.setSignInProvider(userDTO.getSignInProvider());
        user.setEnabled(userDTO.isEnabled());

        User saved = userRepository.saveAndFlush(user);

        UserData userData = userDataRepository.saveAndFlush(UserUtils.newRegisteredUserData(saved));
        saved.setUserData(userData);

        for (Authority authority : userDTO.getAuthorities()) {
            Authority authorityGet = authorityRepository.findByAuthority(authority.getAuthority());
            ((Collection<Authority>) saved.getAuthorities()).add(authorityGet);
        }

        return saved;
    }

    @Override
    public User getUserByUsername(String username) {
        log.debug("Getting user={}", username);
        return userRepository.findByUsername(username);
    }

    @Override
    public Collection<Authority> getRoles() {
        return authorityRepository.findAll();
    }

    @Override
    public List<User> getUsersWithDetail() {
        return userRepository.getUsersWithDetail();
    }

    @Override
    public boolean canAccessUser(CurrentUser currentUser, String username) {
        log.info("Checking if user={} has access to user={}", currentUser, username);
        return currentUser != null && (currentUser.getUser().hasAuthority(Role.ROLE_ADMIN) ||
                currentUser.getUsername().equals(username));
    }

    @Override
    public UserConnection getUserConnectionByUserId(String userId) {
        log.debug("Getting userConnection={}", userId);
        return userConnectionRepository.findByUserId(userId);
    }

    @Override
    public ResetPasswordResult updatePassword(UserPasswordDTO userPasswordDTO) {
        boolean isLoggedIn = userPasswordDTO.getUserId() > 0;
        User user = null;
        Optional<UserToken> userToken = Optional.empty();
        if (isLoggedIn) {
            user = userRepository.findById(userPasswordDTO.getUserId()).orElseThrow(() -> new IllegalArgumentException(("user not found")));
        } else {
            userToken = userTokenRepository.findByToken(userPasswordDTO.getVerificationToken());
            if (userToken.isPresent()) {
                user = userToken.get().getUser();
                if (!isValidToken(user.getId(), userToken.get().getToken())) {
                    user = null;
                }
            }
        }

        if (user == null) {
            return ResetPasswordResult.ERROR;
        } else {
            user.setPassword(UserUtils.bcryptedPassword(userPasswordDTO.getPassword()));
            if (userToken.isPresent()) {
                userTokenRepository.delete(userToken.get());
            }
        }

        if (isLoggedIn) {
            return ResetPasswordResult.LOGGEDIN_SUCCESSFUL;
        } else {
            return ResetPasswordResult.FORGOT_SUCCESSFUL;
        }


    }

    @Override
    public UserToken createUserToken(User user) {
        Optional<UserToken> userToken = userTokenRepository.findByUserId(user.getId());
        if (userToken.isPresent()) {
            userToken.get().updateToken(UUID.randomUUID().toString());
        } else {
            userToken = Optional.of(new UserToken(UUID.randomUUID().toString(), user));
        }

        return userTokenRepository.save(userToken.get());
    }

    @Override
    public Optional<UserToken> getUserToken(String token) {
        return userTokenRepository.findByToken(token);
    }

    @Override
    public User update(UserDTO userDTO) {
        User user = userRepository.findById(userDTO.getUserId()).orElseThrow(() -> new IllegalArgumentException("user not found"));
        user.update(userDTO.getUsername(), userDTO.getFirstName(), userDTO.getLastName(), userDTO.getEmail());
        if (userDTO.isUpdateChildren()) {

            user.getAuthorities().clear();
            for (Authority authority : userDTO.getAuthorities()) {
                Authority match = authorityRepository.findById(authority.getId()).orElseThrow(() -> new IllegalArgumentException("Authority not found"));
                if (!user.getAuthorities().contains(match)) {
                    ((Collection<Authority>) user.getAuthorities()).add(match);
                }
            }
        }
        return user;
    }

    @Override
    public User enableAndApproveUser(User user) {

        UserData userData = user.getUserData();
        userData.setApprovedDatetime(Calendar.getInstance().getTime());

        user.setEnabled(true);
        user.update(userData);

        return user;

    }

    @Override
    public Optional<User> getUserByIdWithDetail(Long ID) {
        return userRepository.findByUserIdWithDetail(ID);
    }

    @Override
    public Authority createAuthority(RoleDTO roleDTO) {
        Authority authority = new Authority();
        authority.setAuthority(roleDTO.getAuthority());
        return authorityRepository.save(authority);
    }

    @Override
    public Authority updateAuthority(RoleDTO roleDTO) {
        Authority authority = authorityRepository.findById(roleDTO.getId()).orElseThrow(() -> new IllegalArgumentException("authority not found"));
        authority.setAuthority(roleDTO.getAuthority());
        return authority;
    }

    @Override
    public Authority getAuthorityById(Long id) {
        return authorityRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Authority not found"));
    }

    @Override
    public void deleteAuthority(Authority authority, Collection<User> users) {
        if (users != null) {
            for (User user : users) {
                user.getAuthorities().remove(authority);
            }
        }
        authorityRepository.delete(authority);
    }

    @Override
    public Collection<User> getUsersByAuthorityId(Long authorityId) {
        return userRepository.findByAuthorityId(authorityId);
    }

    @Override
    public User updateHasAvatar(Long userId, boolean hasAvatar) {
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));
        user.setHasAvatar(hasAvatar);

        CurrentUser currentUser = new CurrentUser(user);

        Authentication authentication =
                new UsernamePasswordAuthenticationToken(
                        currentUser,
                        user.getPassword(),
                        user.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        return user;
    }

    @Override
    public boolean isValidToken(long userId, String token) {
        final Optional<UserToken> userToken = userTokenRepository.findByToken(token);
        boolean isValidToken = false;
        if (userToken.isPresent()) {
            final Calendar cal = Calendar.getInstance();
            UserToken passToken = userToken.get();

            if (passToken.getUser().getId().equals(userId) && (passToken.getTokenExpiration().getTime() - cal.getTime().getTime()) > 0) {
                isValidToken = true;
            }
        }
        return isValidToken;
    }
}
