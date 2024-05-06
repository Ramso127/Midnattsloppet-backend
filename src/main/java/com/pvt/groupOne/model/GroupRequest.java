package com.pvt.groupOne.model;

public class GroupRequest {
    private String teamname;
    private String username;

    public String getUsername() {
        return username;
    }

    public String getTeamname() {
        return teamname;
    }

    @Override
    public String toString() {
        return "GroupRequest [teamname=" + teamname + ", username=" + username + "]";
    }

}

