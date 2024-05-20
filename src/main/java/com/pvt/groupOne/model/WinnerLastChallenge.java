package com.pvt.groupOne.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;


@Entity
public class WinnerLastChallenge {
    
    public WinnerLastChallenge(String groupName) {
        this.groupName = groupName;
    }

    @Id
    @Column(name = "groupName", unique = true)
    private String groupName;

    public String getGroupName() {
        return groupName;
    }


}
