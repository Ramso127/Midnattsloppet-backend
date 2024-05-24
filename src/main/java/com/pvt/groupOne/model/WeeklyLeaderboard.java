package com.pvt.groupOne.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;

@Entity
public class WeeklyLeaderboard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    private RunnerGroup group;

    private int challengeID;
    private double measuredStat;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public RunnerGroup getGroup() {
        return group;
    }

    public void setGroup(RunnerGroup group) {
        this.group = group;
    }

    public int getChallengeID() {
        return challengeID;
    }

    public void setChallengeID(int challengeID) {
        this.challengeID = challengeID;
    }

    public double getMeasuredStat() {
        return measuredStat;
    }

    public void setMeasuredStat(double measuredStat) {
        this.measuredStat = measuredStat;
    }
}
