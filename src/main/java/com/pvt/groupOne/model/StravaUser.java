package com.pvt.groupOne.model;

public class StravaUser {

    private int id;
    private String firstName;
    private String scope;
    private int accessToken;
    private int refreshToken;
    private long expiresAt;

    public StravaUser(int id, String firstName, int accessToken, int refreshToken, long expiresAt) {
        this.firstName = firstName;
        this.id = id;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.expiresAt = expiresAt;
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

    public int getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(int accessToken) {
        this.accessToken = accessToken;
    }

    public int getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(int refreshToken) {
        this.refreshToken = refreshToken;
    }

    public long getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(long expiresAt) {
        this.expiresAt = expiresAt;
    }

}
