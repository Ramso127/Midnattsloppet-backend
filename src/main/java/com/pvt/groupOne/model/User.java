package com.pvt.groupOne.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;

@Entity
public class User {

    @Id
    @Column(name = "user_name")
    private String username;

    @OneToOne(mappedBy = "user")
    private UserInfo userInfo;

    private String password;

    private String email;

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
        if(userInfo == null){
            return "?User= " +
                    "username='" + username + '\'' +
                    ", password='" + password + '\'' + ", email='" + email + '\'';

        }
        else {
            return "User:" +
                    "username='" + username + '\'' +
                    ", userInfo=" + userInfo.toString() +
                    ", password='" + password + '\'' + ", email='" + '\'';
        }

    }
}
