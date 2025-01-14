package com.pvt.groupOne.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToMany;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
public class RunnerGroup {

    public RunnerGroup() {
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer groupId;

    @Column(unique = true)
    private String teamName;
    private String inviteCode;
    private boolean isFull = false;
    
    private int points = 0;
    private String companyName;
    
    
    @Lob
    private byte[] groupPicture;
    
    public boolean isFull() {
        return isFull;
    }
    
    @OneToMany(mappedBy = "runnerGroup")
    @JsonIgnoreProperties("runnerGroup")
    private List<User> users = new ArrayList<>();
    
    public Integer getGroupId() {
        return groupId;
    }
    
    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }
    
    public String getTeamName() {
        return teamName;
    }
    
    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }
    
    public String getInviteCode() {
        return inviteCode;
    }
    
    public void setInviteCode(String inviteCode) {
        this.inviteCode = inviteCode;
    }
    
    public byte[] getGroupPicture() {
        return groupPicture;
    }
    
    public void setGroupPicture(byte[] groupPicture) {
        this.groupPicture = groupPicture;
    }
    
    public List<User> getUsers() {
        return users;
    }
    
    public void setUsers(List<User> users) {
        this.users = users;
    }
    
    public int getPoints() {
        return points;
    }
    
    public void setPoints(int points) {
        this.points = points;
    }
    
    public void addUser(User user) {
        if (!isFull) {
            users.add(user);
            user.setRunnerGroup(this); // Set the association in the User entity
            if (users.size() == 5) {
                isFull = true;
            }
        } else {
            throw new IllegalStateException("Cannot add more users. Group is already full.");
        }
    }
    
    public String getCompanyName() {
        return companyName;
    }
    
    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }
    
    public void setFull(boolean isFull) {
        this.isFull = isFull;
    }
}
