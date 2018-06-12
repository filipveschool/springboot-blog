package com.filip.springbootblog.jpa.services.interfaces;

import com.filip.springbootblog.jpa.models.BatchJob;
import com.filip.springbootblog.jpa.models.GitHubStats;

import java.util.List;

public interface IStatService {

    GitHubStats getCurrentGitHubStats();

    List<BatchJob> getBatchJobsByJob(String jobName);

}
