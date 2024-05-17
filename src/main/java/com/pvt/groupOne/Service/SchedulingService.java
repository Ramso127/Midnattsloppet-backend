package com.pvt.groupOne.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.pvt.groupOne.controller.ChallengeController;

import java.time.LocalDateTime;

@Component
@EnableScheduling
public class SchedulingService {

    @Autowired
    private ChallengeController challengeController;

    @Autowired
    private RunnerGroupService runnerGroupService;

    //every Monday at 00:00 ( 0 seconds, 0 minutes, 0 hours)
    @Scheduled(cron = "0 0 0 * * MON", zone = "Europe/Stockholm")
    public void runTask() {
        runnerGroupService.assignPointsForCurrentChallenge();

        challengeController.incrementChallenges();
        System.out.println("Task executed on " + LocalDateTime.now());
    }
}