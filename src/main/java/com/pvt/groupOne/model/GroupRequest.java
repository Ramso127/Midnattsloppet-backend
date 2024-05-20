package com.pvt.groupOne.model;

public class GroupRequest {
    
    String teamname;
    String username;

    public GroupRequest(String teamname, String username) {
        this.teamname = teamname;
        this.username = username;
    }

    public GroupRequest(){
        
    }
    
    public String getUsername() {
        return username;
    }

    public String getTeamname() {
        return teamname;
    }

}
