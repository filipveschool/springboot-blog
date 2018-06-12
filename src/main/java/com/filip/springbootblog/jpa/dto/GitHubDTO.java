package com.filip.springbootblog.jpa.dto;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class GitHubDTO {

    private long statId = -1;
    private Date statDate;

    @JsonProperty("forks_count")
    private Integer forks = 0;

    @JsonProperty("watchers_count")
    private Integer stars = 0;

    @JsonProperty("subscribers_count")
    private Integer subscribers = 0;

    private Integer followers = 0;

    private Boolean isEmpty = false;

    public GitHubDTO() {

    }

    @Override
    public String toString() {
        return "GitHubDTO{" +
                "statId=" + statId +
                ", forks=" + forks +
                ", stars=" + stars +
                ", subscribers=" + subscribers +
                ", followers=" + followers +
                ", statDate=" + statDate +
                '}';
    }

}
