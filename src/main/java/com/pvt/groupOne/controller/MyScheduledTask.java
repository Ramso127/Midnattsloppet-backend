package com.pvt.groupOne.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.pvt.groupOne.model.Run;
import com.pvt.groupOne.model.User;
import com.pvt.groupOne.repository.RunRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
@EnableScheduling
public class MyScheduledTask {

    @Autowired
    private RunRepository runRepository;

    @Scheduled(cron = "0 37 16 * * TUE", zone = "Europe/Stockholm") // Run every Sunday at midnight
    public void runTask() {
        // Your method logic goes here
        System.out.println("METHOD TEST");
        User testUser = new User("testusername", "testpassword", "testemail@gmail.com", "testcompany");
        LocalDate date = LocalDate.of(2024, 5, 14);
        double distance = 2;
        String totalTime = "00:40:00";
        Run newRun = new Run(date, distance, totalTime, testUser);
        runRepository.save(newRun);
        System.out.println("Task executed on " + LocalDateTime.now());
    }
}