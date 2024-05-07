package com.pvt.groupOne.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;

@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "user_name", unique = true)
    private String username;

    @OneToOne(mappedBy = "user")
    private UserInfo userInfo;

    @JsonIgnore
    private String password;

    private String email;

    private String companyName;

    @ManyToOne
    @JsonIgnoreProperties("users") // Ignore the bidirectional relationship during serialization
    @JoinColumn(name = "runner_group_id")
    private RunnerGroup runnerGroup;

    @OneToOne(mappedBy = "user")
    @JsonIgnoreProperties("user")
    private StravaUser stravaUser;

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

    public UserInfo getUserInfo() {
        return userInfo;
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
        if (!runnerGroup.getUsers().contains(this)) {
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

}
