package com.pvt.groupOne.model;

public class GroupRequest {
    String teamname;
    byte[] image;
    String username;

    public String getUser() {
        return username;
    }

    public byte[] getImage() {
        return image;
    }

    public String getTeamName() {
        return teamname;
    }

}
