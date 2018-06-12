package com.filip.springbootblog.jpa.models;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Date;

@Getter
@Setter
@Entity
@Table(name = "github_stats")
public class GitHubStats {

    @Id
    @Column(name = "stat_id", unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long statId;

    @Basic
    @Column(name = "stat_date", nullable = false)
    private Date statDate;

    @Basic
    @Column(name = "followers", nullable = false)
    private int followers = 0;

    @Basic
    @Column(name = "subscribers", nullable = false)
    private int subscribers = 0;

    @Basic
    @Column(name = "stars", nullable = false)
    private int stars = 0;

    @Basic
    @Column(name = "forks", nullable = false)
    private int forks = 0;


    @Override
    public String toString() {
        return "GitHubStats{" +
                "statId=" + statId +
                ", statDate=" + statDate +
                ", followers=" + followers +
                ", subscribers=" + subscribers +
                ", stars=" + stars +
                ", forks=" + forks +
                '}';
    }

}
