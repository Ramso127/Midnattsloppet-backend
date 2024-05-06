package com.pvt.groupOne.model;

public class GroupRequest {
    String teamname;

    String username;

    public String getUser() {
        return username;
    }

    public String getTeamName() {
        return teamname;
    }

    @Override
    public String toString() {
        return "GroupRequest [teamname=" + teamname + ", username=" + username + "]";
    }

}
