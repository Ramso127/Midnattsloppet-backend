package com.pvt.groupOne.model;

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

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer groupId;

    private String teamName;
    private String inviteCode;
    private boolean isFull = false;

    @Lob
    private byte[] groupPicture;

    public boolean isFull() {
        return isFull;
    }

    @OneToMany(mappedBy = "runnerGroup")
    @JsonIgnoreProperties("runnerGroup") // Ignore the bidirectional relationship during serialization
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

    public void setUsers(ArrayList<User> users) {
        this.users = users;
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

}
