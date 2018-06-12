package com.filip.springbootblog.jpa.services.implementations;

import com.filip.springbootblog.jpa.models.BatchJob;
import com.filip.springbootblog.jpa.models.GitHubStats;
import com.filip.springbootblog.jpa.repositories.BatchJobRepository;
import com.filip.springbootblog.jpa.repositories.GitHubRepository;
import com.filip.springbootblog.jpa.services.interfaces.IStatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("statService")
public class StatServiceImpl implements IStatService {

    @Autowired
    private GitHubRepository gitHubRepository;

    @Autowired
    private BatchJobRepository batchJobRepository;

    @Override
    public GitHubStats getCurrentGitHubStats() {
        return gitHubRepository.findTopByOrOrderByStatDateDesc();
    }

    @Override
    public List<BatchJob> getBatchJobsByJob(String jobName) {
        return batchJobRepository.findByJobName(jobName, sortByJobStartDateDesc());
    }

    private Sort sortByJobStartDateDesc() {
        return new Sort(Sort.Direction.DESC, "startTime");
    }

}
