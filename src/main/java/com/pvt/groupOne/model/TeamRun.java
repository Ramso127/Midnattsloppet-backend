package com.pvt.groupOne.model;

import java.time.LocalDate;

public class TeamRun {

    private String username;
    private LocalDate date;
    private double distance;
    private String time;

    public TeamRun(String username, LocalDate date, double distance, String time) {
        this.username = username;
        this.date = date;
        this.distance = distance;
        this.time = time;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

}
