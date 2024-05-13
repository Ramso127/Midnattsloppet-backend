package com.pvt.groupOne.model;


public class RunRequest {
    private String username;

    private String date;
    private double totaldistance;

    private Integer minutes;

    private Integer seconds;

    public Integer getSeconds() {
        return seconds;
    }

    public void setSeconds(Integer seconds) {
        this.seconds = seconds;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }


    public Integer getMinutes() {
        return minutes;
    }

    public void setMinutes(Integer minutes) {
        this.minutes = minutes;
    }

    public double getTotaldistance() {
        return totaldistance;
    }

    public void setTotaldistance(double totaldistance) {
        this.totaldistance = totaldistance;
    }
}
