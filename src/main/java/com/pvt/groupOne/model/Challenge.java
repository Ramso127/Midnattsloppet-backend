package com.pvt.groupOne.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Challenge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String title;
    private String description;
    private boolean isActive;
    private String measuredStat;

    public Challenge(String title, String description, String measuredStat) {
        this.title = title;
        this.description = description;
        this.measuredStat = measuredStat;
    }

    public Challenge() {

    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean isActive) {
        this.isActive = isActive;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMeasuredStat() {
        return measuredStat;
    }

    public void setMeasuredStat(String measuredStat) {
        this.measuredStat = measuredStat;
    }

}
