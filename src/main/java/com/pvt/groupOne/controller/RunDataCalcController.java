package com.pvt.groupOne.controller;

import com.pvt.groupOne.model.Run;
import com.pvt.groupOne.model.RunnerGroup;
import com.pvt.groupOne.model.TeamRun;
import com.pvt.groupOne.model.User;
import com.pvt.groupOne.repository.RunRepository;
import com.pvt.groupOne.repository.RunnerGroupRepository;
import com.pvt.groupOne.repository.UserImageRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/run")
@CrossOrigin
public class RunDataCalcController {

    @Autowired
    private RunRepository runRepository;

    @Autowired
    private RunnerGroupRepository groupRepository;

    @Autowired
    RunnerGroupRepository runnerGroupRepository;

    @Autowired
    UserImageRepository userImageRepository;

    @GetMapping("/get-user-run-time/{id}")
    public String getUserRunTime(@PathVariable Long id) {
        return runRepository.findRunTimeById(id);
    }

    @GetMapping("/get-user-run-distance/{id}")
    public double getUserRunDistance(@PathVariable Long id) {
        return runRepository.findRunDistanceById(id);
    }

    @GetMapping("/get-user-run-date/{id}")
    public LocalDate getUserRunDate(@PathVariable Long id) {
        return runRepository.findRunDateById(id);
    }

    @GetMapping("/get-user-from-run/{id}")
    public User getUserFromRun(@PathVariable Long id) {
        return runRepository.findUserFromRunById(id);
    }

    @GetMapping("/get-all-user-runs")
    public Map<String, List<Map<String, Object>>> getAllUserRuns(@RequestParam String username) {
        List<Run> runs = runRepository.getAllRunsByUser(username);

        List<Map<String, Object>> dataList = new ArrayList<>();

        for (Run run : runs) {
            Map<String, Object> runMap = new HashMap<>();
            runMap.put("RunID", run.getId());

            Map<String, Object> attributesMap = new HashMap<>();
            attributesMap.put("date", run.getDate().toString()); 
            attributesMap.put("time", run.getTotalTime());
            attributesMap.put("distance", run.getTotalDistance());

            runMap.putAll(attributesMap);

            dataList.add(runMap);
        }

        Map<String, List<Map<String, Object>>> responseMap = new HashMap<>();
        responseMap.put("data", dataList);

        return responseMap;
    }

    @GetMapping("/get-all-run-time-by-user/{username}")
    public List<String> getAllRunTimeByUser(@PathVariable String username) {
        return runRepository.getAllRunTimeByUser(username);
    }

    @GetMapping("/get-all-run-distance-by-user/{username}")
    public List<Double> getAllRunDistanceByUser(@PathVariable String username) {
        return runRepository.getAllRunDistanceByUser(username);
    }

    @GetMapping("/get-all-run-dates/{username}")
    public List<LocalDate> getAllRunDates(@PathVariable String username) {
        return runRepository.getAllRunDates(username);
    }

    @GetMapping("/get-all-run-ids-by-user/{username}")
    public List<Long> getAllRunIdsByUser(@PathVariable String username) {
        return runRepository.getAllRunIdsByUser(username);
    }

    @GetMapping("/get-average-speed/{id}")
    public Map<String, Double> getAverageSpeed(@PathVariable Long id) {
        String runTime = runRepository.findRunTimeById(id);
        double runDistance = runRepository.findRunDistanceById(id);

        String[] runArray = runTime.split(":");
        int hours = Integer.parseInt(runArray[0]);
        int minutes = Integer.parseInt(runArray[1]);

        int totalTimeInMinutes = hours * 60 + minutes;

        double averageSpeed = (runDistance * 60) / totalTimeInMinutes;

        double roundedSpeed = Math.round(averageSpeed * 10.0) / 10.0;
        Map<String, Double> roundedSpeedMap = new HashMap<>();
        roundedSpeedMap.put("averagespeed", roundedSpeed);
        // rounded speed i km/h
        return roundedSpeedMap;
    }

    @GetMapping("/get-average-pace/{id}")
    public Map<String, String> getAveragePace(@PathVariable Long id) {
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
        String formattedPace = String.format("%02d:%02d", minutesPace, secondsPace);
        Map<String, String> averagePaceMap = new HashMap<>();
        averagePaceMap.put("averagepace", formattedPace);
        // Average pace is min/km
        return averagePaceMap;
    }

    @GetMapping("/get-total-distance/{username}")
    public @ResponseBody Map<String, Double> getTotalDistance(@PathVariable String username) {
        List<Double> runDistanceList = runRepository.getAllRunDistanceByUser(username);
        double totalDistance = 0;
        for (double distance : runDistanceList) {
            totalDistance += distance;
        }

        Map<String, Double> response = new HashMap<>();
        response.put("distance", totalDistance);
        return response;
    }

    @GetMapping("/get-total-run-time/{username}")
    public @ResponseBody Map<String, Double> getTotalRunTime(@PathVariable String username) {
        List<String> runTimeList = runRepository.getAllRunTimeByUser(username);
        List<Integer> totalRunTimeList = new ArrayList<>();
        for (String runTime : runTimeList) {
            String[] tempArray = runTime.split(":");
            int hours = Integer.parseInt(tempArray[0]);
            int minutes = Integer.parseInt(tempArray[1]);
            int totalTimeInMinutes = hours * 60 + minutes;
            totalRunTimeList.add(totalTimeInMinutes);
        }

        int totalRunTime = 0;
        for (int time : totalRunTimeList) {
            totalRunTime += time;
        }

        double temp = totalRunTime / 60.0;

        int temp1 = (int) temp;
        double temp2 = (temp - temp1) * 10.0;
        int temp3 = (int) Math.round(temp2);
        double temp4 = temp3 / 10.0;
        double finalNum = temp1 + temp4;

        Map<String, Double> response = new HashMap<>();
        response.put("time", finalNum);
        return response;
    }

    @GetMapping(value = "/get-team-total-runs/{groupname}")
    public @ResponseBody Map<String, Integer> getTeamTotalRuns(@PathVariable String groupname) {
        RunnerGroup runnerGroup = groupRepository.findGroupByTeamName(groupname);
        Map<String, Integer> response = new HashMap<>();
        List<User> list = runnerGroup.getUsers();
        int totalruns = 0;
        for (User user : list) {
            String username = user.getUsername();
            List<Run> runList = runRepository.getAllRunsByUser(username);
            totalruns += runList.size();
        }
        response.put("totalRuns", totalruns);
        return response;
    }

    @GetMapping(value = "/get-team-total-hours/{groupname}")
    public @ResponseBody Map<String, Integer> getTeamTotalHours(@PathVariable String groupname) {
        RunnerGroup runnerGroup = groupRepository.findGroupByTeamName(groupname);
        Map<String, Integer> response = new HashMap<>();
        List<User> list = runnerGroup.getUsers();
        double totalRunTime = 0;
        for (User user : list) {
            String username = user.getUsername();
            List<String> runTimeList = runRepository.getAllRunTimeByUser(username);
            List<Integer> totalRunTimeList = new ArrayList<>();
            totalRunTime += getTotalRunTime(totalRunTime, runTimeList, totalRunTimeList);

        }
        int totalTime = (int) totalRunTime;
        response.put("RunTime", totalTime);
        return response;
    }

    private double getTotalRunTime(double totalRunTime, List<String> runTimeList, List<Integer> totalRunTimeList) {
        for (String runTime : runTimeList) {
            String[] tempArray = runTime.split(":");
            int hours = Integer.parseInt(tempArray[0]);
            int minutes = Integer.parseInt(tempArray[1]);
            int totalTimeInMinutes = hours * 60 + minutes;
            totalRunTimeList.add(totalTimeInMinutes);
        }

        for (int time : totalRunTimeList) {
            totalRunTime += time;
        }

        double temp = totalRunTime / 60.0;

        int temp1 = (int) temp;
        double temp2 = (temp - temp1) * 10.0;
        int temp3 = (int) Math.round(temp2);
        double temp4 = temp3 / 10.0;
        double finalNum = temp1 + temp4;
        totalRunTime = +finalNum;
        return totalRunTime;
    }

    @GetMapping(value = "/get-team-total-distance/{groupname}")
    public @ResponseBody Map<String, Integer> getTeamTotalDistance(@PathVariable String groupname) {
        RunnerGroup runnerGroup = groupRepository.findGroupByTeamName(groupname);
        Map<String, Integer> response = new HashMap<>();
        List<User> list = runnerGroup.getUsers();
        double totalDistance = 0;
        for (User user : list) {
            String username = user.getUsername();
            List<Double> runTimeList = runRepository.getAllRunDistanceByUser(username);
            for (double distance : runTimeList) {
                totalDistance += distance;
                System.out.println(distance + "   " + username);
            }

        }

        response.put("totaldistance", (int) totalDistance);
        return response;
    }

    @GetMapping(value = "/get-latest-team-runs/{username}")
    public @ResponseBody Map<String, List<TeamRun>> getLatestTeamRuns(@PathVariable String username) {

        List<TeamRun> teamRuns = new ArrayList<>();
        List<Run> runs = runnerGroupRepository.findLatestRunsByTeamMemberUsername(username);
        Map<String, List<TeamRun>> myMap = new HashMap<>();

        for (Run run : runs) {
            User user = run.getUser();
            String currentUsername = user.getUsername();
            LocalDate date = run.getDate();
            double distance = run.getTotalDistance();
            String time = run.getTotalTime();

            TeamRun teamRun = new TeamRun(currentUsername, date, distance, time);
            teamRuns.add(teamRun);
        }
        myMap.put("data", teamRuns);

        return myMap;
    }
}