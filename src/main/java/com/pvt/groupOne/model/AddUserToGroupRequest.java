package com.pvt.groupOne.model;

public class AddUserToGroupRequest {

    public AddUserToGroupRequest(String username, String inviteCode) {
        this.username = username;
        this.inviteCode = inviteCode;
    }

    public AddUserToGroupRequest(){
        
    }

    String username;
    String inviteCode;

    public String getInviteCode() {
        return inviteCode;
    }

    public String getUsername() {
        return username;
    }
}
