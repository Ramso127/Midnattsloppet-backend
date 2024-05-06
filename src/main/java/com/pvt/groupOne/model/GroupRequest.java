package com.pvt.groupOne.model;

public class GroupRequest {
    private String teamName;
    private String userName;

    public String getUserName() {
        return userName;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Override
    public String toString() {
        return "GroupRequest [teamname=" + teamName + ", username=" + userName + "]";
    }

}

