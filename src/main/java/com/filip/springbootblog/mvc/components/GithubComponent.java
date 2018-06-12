package com.filip.springbootblog.mvc.components;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.filip.springbootblog.jpa.dto.GitHubDTO;
import com.filip.springbootblog.jpa.models.GitHubStats;
import com.filip.springbootblog.jpa.services.interfaces.IStatService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

@Slf4j
@Component
public class GithubComponent {

    @Autowired
    private Environment environment;

    @Autowired
    private IStatService statService;

    // region GitHub Statistics

    @Cacheable(cacheNames = "githubStats", key = "#root.methodName")
    public GitHubStats getCurrentGitHubStats() {
        return statService.getCurrentGitHubStats();
    }

    public GitHubDTO getGitHubStats() {

        String gitHubRepoUrl = environment.getProperty("github.repo.url");
        String gitHubUserUrl = environment.getProperty("github.user.url");

        // Load Repository JSON elements into GitHubDTO Object

        GitHubDTO gitHubDTO = new GitHubDTO();

        RestTemplate restTemplate = new RestTemplate();
        try {
            gitHubDTO = restTemplate.getForObject(gitHubRepoUrl, GitHubDTO.class);
        } catch (RestClientException e) {
            gitHubDTO.setIsEmpty(true);
            return gitHubDTO;
        }


        // Load User Followers count from GitHub User JSON Endpoint and add to GitHubDTO

        HttpEntity<String> stringUserEntity = restTemplate.getForEntity(gitHubUserUrl, String.class);
        ObjectMapper mapper = new ObjectMapper();
        try {
            ObjectNode node = mapper.readValue(stringUserEntity.getBody(), ObjectNode.class);
            gitHubDTO.setFollowers(node.get("followers").intValue());
        } catch (IOException e) {
            log.error("Error adding follower count from GitHub API to GitHubDTO object");
        }
        return gitHubDTO;
    }

    // endregion

}
