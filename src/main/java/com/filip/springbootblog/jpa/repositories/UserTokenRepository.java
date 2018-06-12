package com.filip.springbootblog.jpa.repositories;

import com.filip.springbootblog.jpa.models.UserToken;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserTokenRepository extends CrudRepository<UserToken, Long> {

    Optional<UserToken> findByToken(String token);

    Optional<UserToken> findByUserId(Long userId);
}
