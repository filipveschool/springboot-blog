package com.filip.springbootblog.jpa.repositories;

import com.filip.springbootblog.jpa.models.UserConnection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserConnectionRepository extends JpaRepository<UserConnection, Long> {

    UserConnection findByUserId(String userId);
}
