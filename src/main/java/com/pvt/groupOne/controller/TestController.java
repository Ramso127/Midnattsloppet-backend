package com.pvt.groupOne.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.pvt.groupOne.Service.RunnerGroupService;
import com.pvt.groupOne.model.RunnerGroup;
import com.pvt.groupOne.model.User;
import com.pvt.groupOne.repository.AccountRepository;
import com.pvt.groupOne.repository.RunnerGroupRepository;

@Controller
@RequestMapping(path = "/testing")
@CrossOrigin
public class TestController {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private RunnerGroupRepository runnerGroupRepository;

    @Autowired
    private RunnerGroupService runnerGroupService;

    @GetMapping(value = "/force-verify/{username}")
    public ResponseEntity<String> forceVerify(@PathVariable String username) {

        if (accountRepository.findByUsername(username) == null) {

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"message\": \"User not found!\"}");
        }

        User currentUser = accountRepository.findByUsername(username);
        currentUser.setVerified(true);
        accountRepository.save(currentUser);
        return ResponseEntity.ok("{\"message\": \"User is verified!\"}");

    }

    @GetMapping(value = "/reset-points")
    public @ResponseBody String resetPoints() {

        Iterable<RunnerGroup> groups = runnerGroupRepository.findAll();
        int totalCounter = 0;
        int pointCounter = 0;

        for (RunnerGroup group : groups) {
            if (group.getPoints() > 0) {
                pointCounter++;
            }
            group.setPoints(0);
            runnerGroupRepository.save(group);
            totalCounter++;
        }

        return "Number of groups: " + totalCounter + ". Number of groups reset: " + pointCounter;
    }

    @GetMapping(value = "/assign-points")
    public @ResponseBody String assignPoints() {

        runnerGroupService.assignPointsForCurrentChallenge();
        return "Points added.";
    }

}
