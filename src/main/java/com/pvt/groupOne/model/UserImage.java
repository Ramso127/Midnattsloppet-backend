package com.pvt.groupOne.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "UserImage")
public class UserImage {

    @Id
    private String userName;

    @OneToOne
    @MapsId
    @JoinColumn(name = "username", referencedColumnName = "username")
    private User user;

    @Lob
    @Column(length = 150000)
    private String base64Image;

    public UserImage(String userName, String base64Image) {
        this.userName = userName;
        this.base64Image = base64Image;
    }

    public UserImage() {
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getBase64Image() {
        return base64Image;
    }

    public void setBase64Image(String base64Image) {
        this.base64Image = base64Image;
    }

    @Override
    public String toString() {
        return "UserImage [userName=" + userName + ", base64Image=" + base64Image + "]";
    }
}