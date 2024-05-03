package com.pvt.groupOne.model;

import com.pvt.groupOne.repository.AccountRepository;
import com.pvt.groupOne.repository.RunnerGroupRepository;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToMany;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Entity
@Component
public class RunnerGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer groupId;

    private String companyName;
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

    private final RunnerGroupRepository runnerRep;

    @Autowired
    public RunnerGroup(RunnerGroupRepository runnerRep) {
        this.runnerRep = runnerRep;
        this.inviteCode = generateInviteCode();
    }

    public Integer getGroupId() {
        return groupId;
    }

    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
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
        users.add(user);
        user.setRunnerGroup(this); // Set the association in the User entity
        if (users.size() == 5) {
            isFull = true;
        }
    }

    private String generateInviteCode() {
        String inviteCode;
        boolean isUnique = false;
        do {
            inviteCode = UUID.randomUUID().toString().substring(0, 8); // You can adjust the length as needed
            // Check if the invite code is unique
            isUnique = !runnerRep.existsByInviteCode(inviteCode);
        } while (!isUnique);
        return inviteCode;
    }

}
