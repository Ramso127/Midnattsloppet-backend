package com.pvt.groupOne.model;

public class GroupStatsRequest {
    private String teamName;
    private double measuredStat;
    private int points;

    public GroupStatsRequest(String teamName, double measuredStat, int points) {
        this.teamName = teamName;
        this.measuredStat = measuredStat;
        this.points = points;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public double getMeasuredStat() {
        return measuredStat;
    }

    public void setMeasuredStat(double measuredStat) {
        this.measuredStat = measuredStat;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }
}
