package com.filip.springbootblog.jpa.repositories;

import com.filip.springbootblog.jpa.models.Authority;
import org.springframework.data.repository.CrudRepository;

import java.util.Collection;

public interface AuthorityRepository extends CrudRepository<Authority, Long> {

    Authority findByAuthority(String authority);

    Collection<Authority> findAll();
}
