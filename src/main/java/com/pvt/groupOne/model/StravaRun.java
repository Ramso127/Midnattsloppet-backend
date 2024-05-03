package com.pvt.groupOne.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class StravaRun {

    @Id
    @Column(name = "date")
    String date;
    double distance;
    int elapsedTime;

    public StravaRun(String date, double distance, int elapsedTime) {
        this.date = date;
        this.distance = distance;
        this.elapsedTime = elapsedTime;
    }

    public StravaRun() {

    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public int getElapsedTime() {
        return elapsedTime;
    }

    public void setElapsedTime(int elapsedTime) {
        this.elapsedTime = elapsedTime;
    }

}
