package com.pvt.groupOne.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;

@Entity
public class StravaUser {

    @Id
    @Column(name = "user_id")
    private int id;

    private String firstName;
    private String scope;
    private String accessToken;
    private String refreshToken;
    private long expiresAt;
    private long timeOfLatestFetchUNIX;

    @OneToOne
    @JoinColumn(name = "user_name", referencedColumnName = "user_name")
    @JsonIgnoreProperties("stravaUser")
    private User user;

    public StravaUser() {

    }

    public StravaUser(int id, String firstName, String accessToken, String refreshToken, long expiresAt,
            long timeOfLatestFetchUNIX) {
        this.id = id;
        this.firstName = firstName;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.expiresAt = expiresAt;
        this.timeOfLatestFetchUNIX = timeOfLatestFetchUNIX;
    }

    public long getTimeOfLatestFetchUNIX() {
        return timeOfLatestFetchUNIX;
    }

    public void setTimeOfLatestFetchUNIX(long timeOfLatestFetchUNIX) {
        this.timeOfLatestFetchUNIX = timeOfLatestFetchUNIX;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public long getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(long expiresAt) {
        this.expiresAt = expiresAt;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "StravaUser [id=" + id + ", firstName=" + firstName + ", scope=" + scope + ", accessToken=" + accessToken
                + ", refreshToken=" + refreshToken + ", expiresAt=" + expiresAt + ", timeOfLatestFetchUNIX="
                + timeOfLatestFetchUNIX + ", user=" + user + "]";
    }
}
