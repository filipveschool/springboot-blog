package com.filip.springbootblog.jpa.repositories;

import com.filip.springbootblog.jpa.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    User findByUsername(String username);

    @Override
    List<User> findAll();

    Optional<User> findById(Long id);

    User save(User user);

    void delete(User user);

    @Override
    boolean existsById(Long userId);

    @Query("select distinct u from User u left join fetch " + "u.authorities left join fetch u.userData p")
    List<User> getUsersWithDetail();

    @Query("select distinct u from User u left join fetch " + "u.authorities left join fetch u.userData p where u.id = ?1")
    Optional<User> findByUserIdWithDetail(Long id);

    Optional<User> findByEmail(String email);

    @Query("select distinct u from User u left join u.authorities a where a.id = ?1")
    List<User> findByAuthorityId(Long id);

    Optional<User> findByUserKey(String userKey);
}
