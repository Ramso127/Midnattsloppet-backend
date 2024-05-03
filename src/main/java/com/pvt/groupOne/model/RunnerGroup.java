package com.pvt.groupOne.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToMany;

import java.util.List;

@Entity
public class RunnerGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer groupId;

    private String teamName;
    private String inviteCode;
    private boolean isFull = false;

    public boolean isFull() {
        return isFull;
    }

    @Lob
    private byte[] groupPicture;

    @OneToMany(mappedBy = "runnerGroup")
    private List<User> users;

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
