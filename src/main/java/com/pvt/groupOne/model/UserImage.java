package com.pvt.groupOne.model;

import jakarta.persistence.*;

@Entity
@Table(name = "UserImage")
public class UserImage {

    @Id
    private String userName;

    @OneToOne
    @JoinColumn(name = "userName", referencedColumnName = "user_name")
    private User user;

    @Lob
    @Column(length = 150000)
    private String base64Image;

    private int length;

    public UserImage(String userName, String base64Image, int length) {
        this.userName = userName;
        this.base64Image = base64Image;
        this.length = length;
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }
}