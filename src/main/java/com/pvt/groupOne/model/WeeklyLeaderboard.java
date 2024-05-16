package com.pvt.groupOne.model;

import org.springframework.data.annotation.Id;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;

@Entity
public class WeeklyLeaderboard {

    @Id
    private RunnerGroup group;

    private int challengeID;
    private double measuredStat;


}
