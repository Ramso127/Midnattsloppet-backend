package com.pvt.groupOne.model;

import jakarta.persistence.*;

import java.util.Date;

@Entity
public class VerificationToken {

    private static final int EXPIRATION = 6 * 60 * 60 * 1000;

    private String token;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToOne(targetEntity = User.class, fetch = FetchType.EAGER)
    @JoinColumn(name = "user_name")
    private User user;

    private Date expiryDate;

    public VerificationToken() {

    }

    public VerificationToken(String token, User user) {
        this.token = token;
        this.user = user;
        long expiryTime = System.currentTimeMillis() + EXPIRATION;
        this.expiryDate = new Date(expiryTime);
    }

    public Date getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Date expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "VerificationToken{" +
                "token='" + token + '\'' +
                ", id=" + id +
                ", user=" + user +
                ", expiryDate=" + expiryDate +
                '}';
    }
}
