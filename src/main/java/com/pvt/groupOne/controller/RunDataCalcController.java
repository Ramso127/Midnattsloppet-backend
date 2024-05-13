package com.pvt.groupOne.controller;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pvt.groupOne.model.Run;
import com.pvt.groupOne.model.User;
import com.pvt.groupOne.repository.AccountRepository;
import com.pvt.groupOne.repository.RunRepository;
import com.pvt.groupOne.repository.RunnerGroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;

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

    @GetMapping ("/getAllUserRuns/{username}")
    public List<Run> getAllUserRuns(@PathVariable String username) {
        return runRepository.getAllRunsByUser(username);
    }

    @GetMapping("/getAllRunTimeByUser/{username}")
    public List<String> getAllRunTimeByUser(@PathVariable String username) {
        return runRepository.getAllRunTimeByUser(username);
    }

    @GetMapping("/getAllRunDistanceByUser/{username}")
    public List<Double> getAllRunDistanceByUser(@PathVariable String username) {
        return runRepository.getAllRunDistanceByUser(username);
    }

    @GetMapping("/getAllRunDates/{username}")
    public List<LocalDate> getAllRunDates(@PathVariable String username) {
        return runRepository.getAllRunDates(username);
    }

    @GetMapping("/getAllRunIdsByUser/{username}")
    public List<Long> getAllRunIdsByUser(@PathVariable String username) {
        return runRepository.getAllRunIdsByUser(username);
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

    @GetMapping("/getTotalDistance/{username}")
    public @ResponseBody Map<String, Integer> getTotalDistance(@PathVariable String username) {
        List<Double> runDistanceList = runRepository.getAllRunDistanceByUser(username);
        double totalDistance = 0;
        for (double distance : runDistanceList) {
            totalDistance += distance;
        }

        Map<String, Integer> response = new HashMap<>();
        response.put("distance", (int) totalDistance);
        return response;
    }

    @GetMapping("/getTotalRunTime/{username}")
    public String getTotalRunTime(@PathVariable String username) {
        List<String> runTimeList = runRepository.getAllRunTimeByUser(username);
        List<Integer> totalRunTimeList = new ArrayList<>();
        for(String runTime : runTimeList) {
            String[] tempArray = runTime.split(":");
            int hours = Integer.parseInt(tempArray[0]);
            int minutes = Integer.parseInt(tempArray[1]);
            int totalTimeInMinutes = hours * 60 + minutes;
            totalRunTimeList.add(totalTimeInMinutes);
        }

        int totalRunTime = 0;
        for(int time : totalRunTimeList) {
            totalRunTime += time;
        }

        double temp = totalRunTime / 60.0;

        int temp1 = (int)temp;
        double temp2 = (temp - temp1) * 10.0;
        int temp3 = (int) Math.round(temp2);
        double test = temp3 / 10.0;
        double finalNum = temp1 + test;


        return "Total run time: " + finalNum + " hours";
    }




}