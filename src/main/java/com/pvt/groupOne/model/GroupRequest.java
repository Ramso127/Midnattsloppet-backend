package com.pvt.groupOne.model;

public class GroupRequest {
    String groupName;
    String companyName;
    byte[] image;
    User user;

    public User getUser() {
        return user;
    }

    public byte[] getImage() {
        return image;
    }

    public String getCompanyName() {
        return companyName;
    }

    public String getTeamName() {
        return groupName;
    }

}
