package com.pvt.groupOne.Service;

import com.pvt.groupOne.repository.AccountRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pvt.groupOne.model.GroupStatsRequest;
import com.pvt.groupOne.model.Challenge;
import com.pvt.groupOne.model.RunnerGroup;
import com.pvt.groupOne.model.User;
import com.pvt.groupOne.model.WinnerLastChallenge;
import com.pvt.groupOne.repository.ChallengeRepository;
import com.pvt.groupOne.repository.RunnerGroupRepository;
import com.pvt.groupOne.repository.WinnerLastChallengeRepository;

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

    @Autowired
    private AccountRepository userRepository;

    @Autowired
    private WinnerLastChallengeRepository winnerLastChallengeRepository;

    public RunnerGroupService(RunnerGroupRepository runnerGroupRepository, ChallengeRepository challengeRepository) {
        this.runnerGroupRepository = runnerGroupRepository;
        this.challengeRepository = challengeRepository;
    }

    public RunnerGroup createRunnerGroup(String teamName, User user, String companyName) {
        RunnerGroup runnerGroup = new RunnerGroup();
        runnerGroup.setTeamName(teamName);
        runnerGroup.addUser(user);
        runnerGroup.setInviteCode(generateInviteCode());
        runnerGroup.setCompanyName(companyName);
        runnerGroupRepository.save(runnerGroup);
        return runnerGroup;
    }

    private String generateInviteCode() {
        String inviteCode;
        boolean isUnique = false;
        do {
            inviteCode = UUID.randomUUID().toString().substring(0, 8);
            isUnique = !runnerGroupRepository.existsByInviteCode(inviteCode);
        } while (!isUnique);
        return inviteCode;
    }

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

    public List<Object[]> getChallengeDataForGroups(Challenge challenge, LocalDate start, LocalDate end) {
        switch (challenge.getId()) {
            case 1:
                return runnerGroupRepository.findGroupsByFurthestDistance(start, end);
            case 2:
                return runnerGroupRepository.findGroupsByMostRuns(start, end);
            case 3:
                return runnerGroupRepository.findGroupsByFurthestRunPerMember(start, end);
            case 4:
                return runnerGroupRepository.findGroupsByHighestAveragePace(start, end);
            case 5:
                return runnerGroupRepository.findGroupsByMostLongRuns(start, end);
            case 6:
                return runnerGroupRepository.findGroupsByTotalNumberOfFasterRuns(start, end);
            default:
                throw new IllegalArgumentException("Unknown challenge type");
        }
    }

    private double getUserChallengeContribution(User user, Challenge challenge, LocalDate start, LocalDate end) {
        Double contribution = 0.0;

        switch (challenge.getId()) {
            case 1:
                contribution = runnerGroupRepository.findUserDistanceForWeek(user.getUsername(), start, end);
                break;
            case 2:
                contribution = runnerGroupRepository.countUserRuns(user.getUsername(), start, end);
                break;
            case 3:
                contribution = runnerGroupRepository.findUserLongestRun(user.getUsername(), start, end);
                break;
            case 4:
                contribution = runnerGroupRepository.calculateUserAveragePace(user.getUsername(), start, end);
                break;
            case 5:
                contribution = runnerGroupRepository.countUserLongRuns(user.getUsername(), start, end);
                break;
            case 6:
                contribution = runnerGroupRepository.countUserFasterRuns(user.getUsername(), start, end);
                break;
            default:
                throw new IllegalArgumentException("Unknown challenge type");
        }
        return contribution != null ? contribution : 0.0;
    }

    public double getGroupChallengeContribution(Integer groupId, Challenge challenge, LocalDate start,
            LocalDate end) {
        Double contribution = 0.0;
        switch (challenge.getId()) {
            case 1:
                contribution = runnerGroupRepository.findGroupDistanceForChallenge(groupId, start, end);
                break;
            case 2:
                contribution = runnerGroupRepository.findGroupRunsForChallenge(groupId, start, end);
                break;
            case 3:
                contribution = runnerGroupRepository.findGroupFurthestRunPerMemberForChallenge(groupId, start, end);
                break;
            case 4:
                contribution = runnerGroupRepository.findGroupAveragePaceForChallenge(groupId, start, end);
                break;
            case 5:
                contribution = runnerGroupRepository.findGroupLongRunsForChallenge(groupId, start, end);
                break;
            case 6:
                contribution = runnerGroupRepository.findGroupFasterRunsForChallenge(groupId, start, end);
                break;
            default:
                throw new IllegalArgumentException("Unknown challenge type");
        }
        return contribution != null ? contribution : 0.0;
    }

    public Map<String, Object> getWeeklyChallengeContribution(String username) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }

        LocalDate startDate = LocalDate.now().with(DayOfWeek.MONDAY);
        LocalDate endDate = LocalDate.now().with(DayOfWeek.SUNDAY);
        Challenge currentChallenge = challengeRepository.findByisActive(true);

        double userContribution = getUserChallengeContribution(user, currentChallenge, startDate, endDate);
        double groupTotal = 0;
        RunnerGroup group = user.getRunnerGroup();
        if (group != null) {
            groupTotal = getGroupChallengeContribution(group.getGroupId(), currentChallenge, startDate, endDate);
        }

        double roundedUserContribution = BigDecimal.valueOf(userContribution).setScale(2, RoundingMode.HALF_UP)
                .doubleValue();
        double roundedGroupTotal = BigDecimal.valueOf(groupTotal).setScale(2, RoundingMode.HALF_UP).doubleValue();

        Map<String, Object> response = new HashMap<>();
        response.put("userContribution", roundedUserContribution);
        response.put("groupTotal", roundedGroupTotal);
        return response;
    }

    public Map<String, List<GroupStatsRequest>> getGroupsSortedByCurrentChallengeWithPoints() {
        Challenge currentChallenge = challengeRepository.findByisActive(true);
        LocalDate startDate = LocalDate.now().with(DayOfWeek.MONDAY);
        LocalDate endDate = LocalDate.now().with(DayOfWeek.SUNDAY);

        List<Object[]> sortedGroups = new ArrayList<>();

        sortedGroups = getChallengeDataForGroups(currentChallenge, startDate, endDate);

        List<GroupStatsRequest> groupsWithPoints = new ArrayList<>();
        int totalGroups = sortedGroups.size();
        for (int i = 0; i < totalGroups; i++) {
            Object[] group = sortedGroups.get(i);
            String teamName = (String) group[0];
            double metric = ((Number) group[1]).doubleValue();

            BigDecimal roundedMetric = BigDecimal.valueOf(metric).setScale(2, RoundingMode.HALF_UP);

            int points = Math.max(25 - i, 1); 
            groupsWithPoints.add(new GroupStatsRequest(teamName, roundedMetric.doubleValue(), points));
        }

        Map<String, List<GroupStatsRequest>> response = new HashMap<>();
        response.put("data", groupsWithPoints);
        return response;
    }

    public void assignPointsForCurrentChallenge() {
        Map<String, List<GroupStatsRequest>> sortedGroupsWithPoints = getGroupsSortedByCurrentChallengeWithPoints();

        List<GroupStatsRequest> groupStatsList = sortedGroupsWithPoints.get("data");
        String winnerTeamName = groupStatsList.get(0).getTeamName();
        WinnerLastChallenge winnerLastChallenge = new WinnerLastChallenge(winnerTeamName);
        Iterable<WinnerLastChallenge> list = winnerLastChallengeRepository.findAll();
        if (list.iterator().hasNext()) {
            winnerLastChallengeRepository.deleteAll();
        }
        winnerLastChallengeRepository.save(winnerLastChallenge);
        for (GroupStatsRequest groupStats : groupStatsList) {
            String teamName = groupStats.getTeamName();
            int points = groupStats.getPoints();

            RunnerGroup group = runnerGroupRepository.findGroupByTeamName(teamName);
            if (group != null) {
                group.setPoints(group.getPoints() + points);
                runnerGroupRepository.save(group);
                System.out.println(
                        "Assigned " + points + " points to group " + teamName + " with " + group.getPoints()
                                + "points");
            }
        }
    }

    @Transactional
    public void deleteRunnerGroup(int groupId) {
        List<User> users = userRepository.findByRunnerGroup_GroupId(groupId);
        for (User user : users) {
            user.setRunnerGroup(null);
            userRepository.save(user);
        }

        runnerGroupRepository.deleteById(groupId);
    }
}
