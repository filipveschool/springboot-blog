package com.filip.springbootblog.jpa.services.interfaces;

import com.filip.springbootblog.jpa.dto.RoleDTO;
import com.filip.springbootblog.jpa.dto.UserDTO;
import com.filip.springbootblog.jpa.dto.UserPasswordDTO;
import com.filip.springbootblog.jpa.enums.ResetPasswordResult;
import com.filip.springbootblog.jpa.models.Authority;
import com.filip.springbootblog.jpa.models.CurrentUser;
import com.filip.springbootblog.jpa.models.User;
import com.filip.springbootblog.jpa.models.UserConnection;
import com.filip.springbootblog.jpa.models.UserToken;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface IUserService {

    Optional<User> getUserById(Long id);

    Optional<User> getUserByEmail(String email);

    @Transactional(readOnly = true)
    User getByUserKey(String userKey);

    Collection<User> getAllUsers();

    User create(UserDTO userDTO);

    User getUserByUsername(String username);

    @Transactional(readOnly = true)
    Collection<Authority> getRoles();

    List<User> getUsersWithDetail();

    boolean canAccessUser(CurrentUser currentUser, String username);

    UserConnection getUserConnectionByUserId(String userId);

    @Transactional
    ResetPasswordResult updatePassword(UserPasswordDTO userPasswordDTO);

    @Transactional
    UserToken createUserToken(User user);

    @Transactional
    Optional<UserToken> getUserToken(String token);

    User update(UserDTO userDTO);

    @Transactional
    User enableAndApproveUser(User user);

    @Transactional(readOnly = true)
    Optional<User> getUserByIdWithDetail(Long ID);

    Authority createAuthority(RoleDTO roleDTO);

    Authority updateAuthority(RoleDTO roleDTO);

    Authority getAuthorityById(Long id);

    void deleteAuthority(Authority authority, Collection<User> users);

    Collection<User> getUsersByAuthorityId(Long authorityId);

    User updateHasAvatar(Long userId, boolean hasAvatar);

    boolean isValidToken(long userId, String token);

}
