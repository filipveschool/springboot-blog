package com.filip.springbootblog.jpa.repositories;

import com.filip.springbootblog.jpa.models.GitHubStats;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GitHubRepository extends JpaRepository<GitHubStats, Long> {

    @Override
    GitHubStats getOne(Long statId);

    GitHubStats findTopByOrOrderByStatDateDesc();
}
