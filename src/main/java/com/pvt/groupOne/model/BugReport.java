package com.pvt.groupOne.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class BugReport {

    public BugReport(String report) {

        this.report = report;
    }

    public BugReport() {

    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String report;

    public int getId() {
        return id;
    }

    public String getReport() {
        return report;
    }

    public void setReport(String report) {
        this.report = report;
    }

}
