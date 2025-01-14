package com.pvt.groupOne.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import java.util.*;

@Entity
public class User {

    public User(String username, String password, String email, String companyName) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.companyName = companyName;
    }

    @Id
    @Column(name = "user_name", unique = true)
    private String username;

    @JsonIgnore
    private String password;

    private String email;

    private String companyName;

    @Column(name = "points")
    private Integer points;

    private boolean isVerified = false;


    @ManyToOne
    @JsonIgnoreProperties("users")
    @JoinColumn(name = "runner_group_id")
    private RunnerGroup runnerGroup;

    @OneToOne(mappedBy = "user")
    @JsonIgnoreProperties("user")
    private StravaUser stravaUser;

    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER)
    @JsonIgnoreProperties("user")
    private List<Run> runs = new ArrayList<>();

    public User() {

    }

    public StravaUser getStravaUser() {
        return stravaUser;
    }

    public void setStravaUser(StravaUser stravaUser) {
        this.stravaUser = stravaUser;
    }

    public String getUserName() {
        return username;
    }

    public void setUserName(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "{\"username\": \"" + username + "\", \"email\": \"" + email + "\"}";
    }

    public void setRunnerGroup(RunnerGroup runnerGroup) {
        this.runnerGroup = runnerGroup;
        if (runnerGroup != null && !runnerGroup.getUsers().contains(this)) {
            runnerGroup.getUsers().add(this);
        }
    }

    public String getUsername() {
        return username;
    }

    public RunnerGroup getRunnerGroup() {
        return runnerGroup;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public Integer getPoints() {
        return points;
    }

    public void setPoints(Integer points) {
        this.points = points;
    }

    public boolean isVerified() {
        return isVerified;
    }

    public void setVerified(boolean isVerified) {
        this.isVerified = isVerified;
    }

}
