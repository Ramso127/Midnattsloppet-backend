package com.pvt.groupOne.Service;

import org.springframework.stereotype.Service;

import com.pvt.groupOne.model.GroupStatsRequest;
import com.pvt.groupOne.model.Challenge;
import com.pvt.groupOne.model.RunnerGroup;
import com.pvt.groupOne.model.User;
import com.pvt.groupOne.repository.ChallengeRepository;
import com.pvt.groupOne.repository.RunnerGroupRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class RunnerGroupService {

    private final RunnerGroupRepository runnerGroupRepository;
    private final ChallengeRepository challengeRepository;

    public RunnerGroupService(RunnerGroupRepository runnerGroupRepository, ChallengeRepository challengeRepository) {
        this.runnerGroupRepository = runnerGroupRepository;
        this.challengeRepository = challengeRepository;
    }

    public RunnerGroup createRunnerGroup(String teamName, User user) {
        RunnerGroup runnerGroup = new RunnerGroup();
        runnerGroup.setTeamName(teamName);
        runnerGroup.addUser(user);
        runnerGroup.setInviteCode(generateInviteCode());
        runnerGroupRepository.save(runnerGroup);
        return runnerGroup;
    }

    private String generateInviteCode() {
        String inviteCode;
        boolean isUnique = false;
        do {
            inviteCode = UUID.randomUUID().toString().substring(0, 8); // You can adjust the length as needed
            // Check if the invite code is unique
            isUnique = !runnerGroupRepository.existsByInviteCode(inviteCode);
        } while (!isUnique);
        return inviteCode;
    }

    // Get a sorted list of team members based on distance for a specific group
    public List<Object[]> getSortedTeamMembersByDistance(Integer groupId) {
        return runnerGroupRepository.findTeamMembersByDistance(groupId);
    }

    public List<Object[]> getTopGroupsByDistanceForWeek(LocalDate startOfWeek) {
        LocalDate endOfWeek = startOfWeek.plusDays(6);
        return runnerGroupRepository.findTopGroupsByDistance(startOfWeek, endOfWeek);
    }

    public List<Object[]> getTopGroupsByDistanceForMonth(LocalDate startOfMonth) {
        LocalDate endOfMonth = startOfMonth.with(TemporalAdjusters.lastDayOfMonth());
        return runnerGroupRepository.findTopGroupsByDistance(startOfMonth, endOfMonth);
    }

    public List<Object[]> getTopGroupsByTotalDistance() {
        return runnerGroupRepository.findTopGroupsByTotalDistance();
    }

    public Map<String, List<GroupStatsRequest>> getGroupsSortedByCurrentChallengeWithPoints() {
        Challenge currentChallenge = challengeRepository.findByisActive(true);
        LocalDate startDate = LocalDate.now().with(DayOfWeek.MONDAY);
        LocalDate endDate = LocalDate.now().with(DayOfWeek.SUNDAY);

        List<Object[]> sortedGroups = new ArrayList<>();

        switch (currentChallenge.getId()) {
            case 1:
                sortedGroups = runnerGroupRepository.findGroupsByFurthestDistance(startDate, endDate);
                break;
            case 2:
                sortedGroups = runnerGroupRepository.findGroupsByMostRuns(startDate, endDate);
                break;
            case 3:
                sortedGroups = runnerGroupRepository.findGroupsByFurthestRunPerMember(startDate, endDate);
                break;
            case 4:
                sortedGroups = runnerGroupRepository.findGroupsByHighestAveragePace(startDate, endDate);
                break;
            case 5:
                sortedGroups = runnerGroupRepository.findGroupsByMostLongRuns(startDate, endDate);
                break;
            case 6:
                sortedGroups = runnerGroupRepository.findGroupsByTotalNumberOfFasterRuns(startDate, endDate);
                break;
            default:
                throw new IllegalArgumentException("Unknown challenge type");
        }

        List<GroupStatsRequest> groupsWithPoints = new ArrayList<>();
        int totalGroups = sortedGroups.size();
        for (int i = 0; i < totalGroups; i++) {
            Object[] group = sortedGroups.get(i);
            String teamName = (String) group[0];
            double metric = ((Number) group[1]).doubleValue(); // Ensure correct casting

            // Round the metric to 2 decimal places
            BigDecimal roundedMetric = BigDecimal.valueOf(metric).setScale(2, RoundingMode.HALF_UP);

            int points = Math.max(25 - i, 1); // Calculate points
            groupsWithPoints.add(new GroupStatsRequest(teamName, roundedMetric.doubleValue(), points));
        }

        Map<String, List<GroupStatsRequest>> response = new HashMap<>();
        response.put("data", groupsWithPoints);
        return response;
    }

    public void assignPointsForCurrentChallenge() {
        // Get sorted groups with points
        Map<String, List<GroupStatsRequest>> sortedGroupsWithPoints = getGroupsSortedByCurrentChallengeWithPoints();

        // Iterate over the groups and assign points to each group
        List<GroupStatsRequest> groupStatsList = sortedGroupsWithPoints.get("data");
        for (GroupStatsRequest groupStats : groupStatsList) {
            String teamName = groupStats.getTeamName();
            int points = groupStats.getPoints();

            // Find the group and assign points
            RunnerGroup group = runnerGroupRepository.findGroupByTeamName(teamName);
            if (group != null) {
                group.setPoints(group.getPoints() + points);
                runnerGroupRepository.save(group);
                System.out.println(
                        "Assigned " + points + " points to group " + teamName + " with " + group.getPoints() + "points");
            }
        }
    }
}
