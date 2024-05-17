package com.pvt.groupOne.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.pvt.groupOne.controller.ChallengeController;
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

    private ChallengeController challengeController = new ChallengeController();

    // TEMPLATE Run every monday at 0 sec, 0 minutes, 0 hours (00:00)
    @Scheduled(cron = "0 00 00 * * MON", zone = "Europe/Stockholm")
    public void runTask() {

        // Logic
        challengeController.incrementChallenges();
        System.out.println("Task executed on " + LocalDateTime.now());
    }
}