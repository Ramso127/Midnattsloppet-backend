package com.pvt.groupOne.controller;

import com.pvt.groupOne.model.GroupStatsRequest;
import com.pvt.groupOne.model.Run;
import com.pvt.groupOne.Service.RunService;
import com.pvt.groupOne.Service.RunnerGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/activity")
public class ActivityController {

    @Autowired
    private RunService runService;

    @Autowired
    private RunnerGroupService runnerGroupService;

    //top 3 runs for a user
    @GetMapping("/users/{username}/top-runs")
    public ResponseEntity<List<Run>> getTopRunsByUser(@PathVariable String username) {
        List<Run> runs = runService.getTopThreeRunsByUser(username);
        return ResponseEntity.ok(runs);
    }

    //top 3 users based on distance run during the current week
    // ----------------
    @GetMapping("/users/top-weekly-runners")
    public ResponseEntity<List<Object[]>> getTopWeeklyRunners(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startOfWeek,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endOfWeek) {
        List<Object[]> users = runService.getTopThreeUsersByDistanceThisWeek();
        return ResponseEntity.ok(users);
    }

    //sorted list of team members by distance
    @GetMapping("/runner-groups/{groupId}/members-sorted-by-distance")
    public ResponseEntity<List<Object[]>> getMembersSortedByDistance(@PathVariable Integer groupId) {
        List<Object[]> members = runnerGroupService.getSortedTeamMembersByDistance(groupId);
        return ResponseEntity.ok(members);
    }

    @GetMapping("/top-weekly")
    public ResponseEntity<List<Object[]>> getTopGroupsByWeek() {
        LocalDate startOfWeek = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        List<Object[]> groups = runnerGroupService.getTopGroupsByDistanceForWeek(startOfWeek);
        return ResponseEntity.ok(groups);
    }

    @GetMapping("/top-monthly")
    public ResponseEntity<List<Object[]>> getTopGroupsByMonth() {
        LocalDate startOfMonth = LocalDate.now().with(TemporalAdjusters.firstDayOfMonth());
        List<Object[]> groups = runnerGroupService.getTopGroupsByDistanceForMonth(startOfMonth);
        return ResponseEntity.ok(groups);
    }

    @GetMapping("/top-all-time")
    public ResponseEntity<List<Object[]>> getTopGroupsByAllTime() {
        List<Object[]> groups = runnerGroupService.getTopGroupsByTotalDistance();
        return ResponseEntity.ok(groups);
    }

    @GetMapping("/sorted-groups-by-challenge")
    public ResponseEntity<Map<String, List<GroupStatsRequest>>> getSortedGroupsByChallenge() {
        Map<String, List<GroupStatsRequest>> sortedGroups = runnerGroupService
                .getGroupsSortedByCurrentChallengeWithPoints();
        return ResponseEntity.ok(sortedGroups);
    }

    @GetMapping("/challenge-contribution/{username}")
    public ResponseEntity<Map<String, Object>> getChallengeContribution(@PathVariable String username) {
        Map<String, Object> challengeData = runnerGroupService.getWeeklyChallengeContribution(username);
        return ResponseEntity.ok(challengeData);
    }
}
