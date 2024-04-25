package com.pvt.groupOne.model;

import com.pvt.groupOne.repository.PasswordTokenRepository;
import jakarta.persistence.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.Date;

@Entity
public class PasswordResetToken {

    private static final int EXPIRATION = 24 * 60 * 60 * 1000;

    private String token;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToOne(targetEntity = User.class, fetch = jakarta.persistence.FetchType.EAGER)
    private User user;

    private Date expiryDate;

    public PasswordResetToken() {

    }

    public PasswordResetToken(String token, User user)  {
        this.token = token;
        this.user = user;
        long expiryTime = System.currentTimeMillis() + EXPIRATION;
        this.expiryDate = new Date(expiryTime);
    }

    public Date getExpiryDate() {
        return expiryDate;
    }


}
