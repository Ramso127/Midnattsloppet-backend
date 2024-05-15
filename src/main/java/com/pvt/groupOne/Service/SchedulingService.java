package com.pvt.groupOne.Service;

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
public class SchedulingService {

    @Autowired
    private RunRepository runRepository;

    // TEMPLATE Run every tuesday at 0 sec, 42 minutes, 16 hours (16:42)
    @Scheduled(cron = "0 42 16 * * TUE", zone = "Europe/Stockholm")
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