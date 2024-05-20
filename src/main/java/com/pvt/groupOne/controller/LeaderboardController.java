package com.pvt.groupOne.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.pvt.groupOne.Service.RunnerGroupService;
import com.pvt.groupOne.model.GroupStatsRequest;
import com.pvt.groupOne.model.RunnerGroup;
import com.pvt.groupOne.model.WinnerLastChallenge;
import com.pvt.groupOne.repository.RunnerGroupRepository;
import com.pvt.groupOne.repository.WinnerLastChallengeRepository;

@RestController
@RequestMapping("/leaderboard")
public class LeaderboardController {

    @Autowired
    private RunnerGroupRepository runnerGroupRepository;
    @Autowired
    private WinnerLastChallengeRepository winnerLastChallengeRepository;

    @GetMapping("/weekly-by-points")
    public List<RunnerGroup> getSortedGroupsByMembersPoints() {
        return runnerGroupRepository.findGroupsOrderByMembersTotalPoints();
    }

    @GetMapping("/winner-last-challenge")
    public Map<String, String> winnerLastChallenge() {
        Iterable<WinnerLastChallenge> list = winnerLastChallengeRepository.findAll();

        Map<String, String> winnerMap = new HashMap<>();
        if (!list.iterator().hasNext()) {
            winnerMap.put("weeklywinner", "no winner");
            return winnerMap;
        }
        WinnerLastChallenge winner = list.iterator().next();
        String winnerName = winner.getGroupName();
        winnerMap.put("weeklywinner", winnerName);
        return winnerMap;
    }

    @GetMapping("/all-time-by-points")
    public List<RunnerGroup> getGroupsSortedByPoints() {
        return runnerGroupRepository.findAllGroupsOrderByPoints();
    }
}
