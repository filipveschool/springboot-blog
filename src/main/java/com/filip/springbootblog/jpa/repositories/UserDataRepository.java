package com.filip.springbootblog.jpa.repositories;

import com.filip.springbootblog.jpa.models.UserData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserDataRepository extends JpaRepository<UserData, Long> {

    UserData findByUserId(Long userId);

    @Override
    List<UserData> findAll();
}
