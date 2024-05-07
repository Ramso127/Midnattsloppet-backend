package com.pvt.groupOne.controller;
import com.pvt.groupOne.model.User;
import com.pvt.groupOne.repository.AccountRepository;
import com.pvt.groupOne.repository.RunRepository;
import com.pvt.groupOne.repository.RunnerGroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Date;

@RestController
@RequestMapping("/run")
@CrossOrigin
public class RunDataCalcController {

    @Autowired
    private RunnerGroupRepository runnerGroupRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private RunRepository runRepository;


    @GetMapping ("/getUserRunTime/{id}")
    public String getUserRunTime(@PathVariable Long id) {
        return runRepository.findRunTimeById(id);
    }

    @GetMapping ("/getUserRunDistance/{id}")
    public double getUserRunDistance(@PathVariable Long id) {
        return runRepository.findRunDistanceById(id);
    }

    @GetMapping ("/getUserRunDate/{id}")
    public LocalDate getUserRunDate(@PathVariable Long id) {
        return runRepository.findRunDateById(id);
    }

    @GetMapping ("/getUserFromRun/{id}")
    public User getUserFromRun(@PathVariable Long id) {
        return runRepository.findUserFromRunById(id);
    }

    @GetMapping ("/getAverageSpeed/{id}")
    public String getAverageSpeed(@PathVariable Long id) {
        String runTime = runRepository.findRunTimeById(id);
        double runDistance = runRepository.findRunDistanceById(id);

        String[] runArray = runTime.split(":");
        int hours = Integer.parseInt(runArray[0]);
        int minutes = Integer.parseInt(runArray[1]);

        int totalTimeInMinutes = hours * 60 + minutes;

        double averageSpeed = (runDistance * 60) / totalTimeInMinutes;

        double roundedSpeed = Math.round(averageSpeed * 10.0) / 10.0;

        return "The average speed for the run was " + roundedSpeed + " km/h";
    }

    @GetMapping ("/getAveragePace/{id}")
    public String getAveragePace(@PathVariable Long id) {
        String runTime = runRepository.findRunTimeById(id);
        double runDistance = runRepository.findRunDistanceById(id);

        String[] runArray = runTime.split(":");
        int hours = Integer.parseInt(runArray[0]);
        int minutes = Integer.parseInt(runArray[1]);

        int totalTimeInMinutes = hours * 60 + minutes;

        double averagePace = totalTimeInMinutes / runDistance;

        double roundedPace = Math.round(averagePace * 10.0) / 10.0;

        int minutesPace = (int) roundedPace;
        double secondsFraction = roundedPace - minutesPace;
        int secondsPace = (int) Math.round(secondsFraction * 60);


        return "The average pace for the run was " + minutesPace+":"+secondsPace + " min/km";
    }



}