package com.pvt.groupOne.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import com.pvt.groupOne.model.RunnerGroup;
import com.pvt.groupOne.repository.RunnerGroupRepository;

@RestController
@RequestMapping("/leaderboard")
public class LeaderboardController {

    @Autowired
    private RunnerGroupRepository runnerGroupRepository;

    @GetMapping("/weekly-by-points")
    public List<RunnerGroup> getSortedGroupsByMembersPoints() {
        return runnerGroupRepository.findGroupsOrderByMembersTotalPoints();
    }

    @GetMapping("/all-time-by-points")
    public List<RunnerGroup> getGroupsSortedByPoints() {
        return runnerGroupRepository.findAllGroupsOrderByPoints();
    }
}