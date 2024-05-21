package com.pvt.groupOne.controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.pvt.groupOne.Service.RunnerGroupService;
import com.pvt.groupOne.model.AddUserToGroupRequest;
import com.pvt.groupOne.model.GroupRequest;
import com.pvt.groupOne.model.Run;
import com.pvt.groupOne.model.RunnerGroup;
import com.pvt.groupOne.model.User;
import com.pvt.groupOne.repository.AccountRepository;
import com.pvt.groupOne.repository.ChallengeRepository;
import com.pvt.groupOne.repository.RunRepository;
import com.pvt.groupOne.repository.RunnerGroupRepository;
import org.springframework.web.bind.annotation.RequestParam;


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

    @Autowired
    private RunRepository runRepository;

    @GetMapping(value = "/force-verify/{username}")
    public ResponseEntity<String> forceVerify(@PathVariable String username) {
        User currentUser = accountRepository.findByUsername(username);

        if (currentUser == null) {

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"message\": \"User not found!\"}");
        }

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

    @GetMapping(value = "/create-demo-teams")
    public @ResponseBody String createDemoTeams() {

        // CREATE AND SAVE USERS

        LocalDate today = LocalDate.now();

        User firstUser = new User("firstUser", "12345678", "firstemail@gmail.com", "Demo Company");
        User secondUser = new User("secondUser", "23456789", "secondemail@gmail.com", "Demo Company");
        User thirdUser = new User("thirdUser", "34567890", "thirdemail@gmail.com", "Demo Company");
        User fourthUser = new User("fourthUser", "45678901", "fourthemail@gmail.com", "Demo Company");
        User fifthUser = new User("fifthUser", "56789012", "fifthemail@gmail.com", "Demo Company");
        User sixthUser = new User("sixthUser", "67890123", "sixthemail@gmail.com", "Demo Company");
        User seventhUser = new User("seventhUser", "78901234", "seventhemail@gmail.com", "Demo Company");
        User eighthUser = new User("eighthUser", "89012345", "eighthemail@gmail.com", "Demo Company");
        User ninthUser = new User("ninthUser", "90123456", "ninthemail@gmail.com", "Demo Company");
        User tenthUser = new User("tenthUser", "01234567", "tenthemail@gmail.com", "Demo Company");
        User eleventhUser = new User("eleventhUser", "12345098", "eleventhemail@gmail.com", "Demo Company");
        User twelfthUser = new User("twelfthUser", "23456109", "twelfthemail@gmail.com", "Demo Company");

        ArrayList<User> userList = new ArrayList<>();

        Collections.addAll(userList, firstUser, secondUser, thirdUser, fourthUser, fifthUser, sixthUser, seventhUser,
                eighthUser, ninthUser, tenthUser, eleventhUser, twelfthUser);

        accountRepository.saveAll(userList);

        // CREATE AND SAVE RUNS

        // FAST RUNNERS (4-5 MINUTES / KM)

        Run firstUserFirstRun = new Run(today.minusDays(1), 5.1, "00:25:31", firstUser);
        Run firstUserSecondRun = new Run(today.minusDays(2), 3.1, "00:13:53", firstUser);
        Run firstUserThirdRun = new Run(today.minusDays(3), 9.2, "00:44:31", firstUser);

        Run secondUserFirstRun = new Run(today.minusDays(4), 13.2, "01:03:54", secondUser);
        Run secondUserSecondRun = new Run(today.minusDays(2), 11.1, "00:54:44", secondUser);
        Run secondUserThirdRun = new Run(today.minusDays(3), 7.3, "00:33:06", secondUser);

        Run thirdUserFirstRun = new Run(today.minusDays(1), 3.2, "00:12:40", thirdUser);
        Run thirdUserSecondRun = new Run(today, 6.4, "00:28:49", thirdUser);
        Run thirdUserThirdRun = new Run(today.minusDays(2), 1, "00:04:14", thirdUser);

        Run fourthUserFirstRun = new Run(today.minusDays(1), 5.2, "00:22:48", fourthUser);
        Run fourthUserSecondRun = new Run(today, 7.2, "00:32:36", fourthUser);
        Run fourthUserThirdRun = new Run(today.minusDays(4), 5.1, "00:21:55", fourthUser);

        // MEDIUM RUNNERS (5-6 MINUTES / KM)

        Run fifthUserFirstRun = new Run(today, 6.2, "00:32:27", fifthUser);
        Run fifthUserSecondRun = new Run(today.minusDays(2), 9.3, "00:53:43", fifthUser);
        Run fifthUserThirdRun = new Run(today.minusDays(4), 5.1, "00:26:55", fifthUser);

        Run sixthUserFirstRun = new Run(today.minusDays(4), 13.3, "01:10:02", sixthUser);
        Run sixthUserSecondRun = new Run(today.minusDays(2), 11.2, "01:02:11", sixthUser);
        Run sixthUserThirdRun = new Run(today.minusDays(3), 9, "00:52:35", sixthUser);

        Run seventhUserFirstRun = new Run(today.minusDays(6), 4.2, "00:22:36", seventhUser);
        Run seventhUserSecondRun = new Run(today.minusDays(7), 6.1, "00:34:26", seventhUser);
        Run seventhUserThirdRun = new Run(today.minusDays(4), 2.1, "00:10:40", seventhUser);

        Run eighthUserFirstRun = new Run(today.minusDays(1), 15.6, "01:24:48", eighthUser);
        Run eighthUserSecondRun = new Run(today.minusDays(2), 16.4, "01:35:21", eighthUser);
        Run eighthUserThirdRun = new Run(today, 1, "01:04:55", eighthUser);

        // SLOW RUNNERS (6-8 MINUTES / KM)

        Run ninthUserFirstRun = new Run(today.minusDays(8), 2, "00:13:55", ninthUser);
        Run ninthUserSecondRun = new Run(today.minusDays(4), 4.2, "00:27:11", ninthUser);
        Run ninthUserThirdRun = new Run(today.minusDays(6), 5.1, "00:34:29", ninthUser);

        Run tenthUserFirstRun = new Run(today.minusDays(2), 19.5, "02:28:05", tenthUser);
        Run tenthUserSecondRun = new Run(today.minusDays(3), 6.2, "00:43:06", tenthUser);
        Run tenthUserThirdRun = new Run(today.minusDays(1), 4.3, "00:28:28", tenthUser);

        Run eleventhUserFirstRun = new Run(today.minusDays(4), 3.1, "00:22:13", eleventhUser);
        Run eleventhUserSecondRun = new Run(today.minusDays(2), 5.2, "00:37:26", eleventhUser);
        Run eleventhUserThirdRun = new Run(today, 5.1, "00:31:01", eleventhUser);

        Run twelfthUserFirstRun = new Run(today.minusDays(5), 8.4, "01:00:21", twelfthUser);
        Run twelfthUserSecondRun = new Run(today.minusDays(2), 2, "00:12:19", twelfthUser);
        Run twelfthUserThirdRun = new Run(today, 4.2, "00:30:17", twelfthUser);

        ArrayList<Run> runList = new ArrayList<>();

        Collections.addAll(runList, firstUserFirstRun, firstUserSecondRun, firstUserThirdRun, secondUserFirstRun,
                secondUserSecondRun, secondUserThirdRun, thirdUserFirstRun, thirdUserSecondRun, thirdUserThirdRun,
                fourthUserFirstRun, fourthUserSecondRun, fourthUserThirdRun, fifthUserFirstRun, fifthUserSecondRun,
                fifthUserThirdRun, sixthUserFirstRun, sixthUserSecondRun, sixthUserThirdRun, seventhUserFirstRun,
                seventhUserSecondRun, seventhUserThirdRun, eighthUserFirstRun, eighthUserSecondRun, eighthUserThirdRun,
                ninthUserFirstRun, ninthUserSecondRun, ninthUserThirdRun, tenthUserFirstRun, tenthUserSecondRun,
                tenthUserThirdRun, eleventhUserFirstRun, eleventhUserSecondRun, eleventhUserThirdRun,
                twelfthUserFirstRun, twelfthUserSecondRun, twelfthUserThirdRun);

        runRepository.saveAll(runList);

        // CREATE AND SAVE TEAMS

        addGroup(new GroupRequest("Fast Team", firstUser.getUsername()));
        addGroup(new GroupRequest("Medium Team", fifthUser.getUsername()));
        addGroup(new GroupRequest("Slow Team", ninthUser.getUsername()));

        RunnerGroup fastGroup = runnerGroupRepository.findGroupByTeamName("Fast Team");
        RunnerGroup mediumGroup = runnerGroupRepository.findGroupByTeamName("Medium Team");
        RunnerGroup slowGroup = runnerGroupRepository.findGroupByTeamName("Slow Team");

        AddUserToGroupRequest secondGroupRequest = new AddUserToGroupRequest(secondUser.getUsername(),
                fastGroup.getInviteCode());
        AddUserToGroupRequest thirdGroupRequest = new AddUserToGroupRequest(thirdUser.getUsername(),
                fastGroup.getInviteCode());
        AddUserToGroupRequest fourthGroupRequest = new AddUserToGroupRequest(fourthUser.getUsername(),
                fastGroup.getInviteCode());
        AddUserToGroupRequest sixthGroupRequest = new AddUserToGroupRequest(sixthUser.getUsername(),
                mediumGroup.getInviteCode());
        AddUserToGroupRequest seventhGroupRequest = new AddUserToGroupRequest(seventhUser.getUsername(),
                mediumGroup.getInviteCode());
        AddUserToGroupRequest eighthGroupRequest = new AddUserToGroupRequest(eighthUser.getUsername(),
                mediumGroup.getInviteCode());
        AddUserToGroupRequest tenthGroupRequest = new AddUserToGroupRequest(tenthUser.getUsername(),
                slowGroup.getInviteCode());
        AddUserToGroupRequest eleventhGroupRequest = new AddUserToGroupRequest(eleventhUser.getUsername(),
                slowGroup.getInviteCode());
        AddUserToGroupRequest twelfthGroupRequest = new AddUserToGroupRequest(twelfthUser.getUsername(),
                slowGroup.getInviteCode());

        addUserToGroup(secondGroupRequest);
        addUserToGroup(thirdGroupRequest);
        addUserToGroup(sixthGroupRequest);
        addUserToGroup(seventhGroupRequest);
        addUserToGroup(eighthGroupRequest);
        addUserToGroup(tenthGroupRequest);
        addUserToGroup(eleventhGroupRequest);
        addUserToGroup(twelfthGroupRequest);
        addUserToGroup(fourthGroupRequest);

        return "Everything has been added.";

    }

    @GetMapping(value = "/remove-all-demo-users")
    public @ResponseBody String removeAllDemoUsers(){
        removeUser("firstUser");
        removeUser("secondUser");
        removeUser("thirdUser");
        removeUser("fourthUser");
        removeUser("fifthUser");
        removeUser("sixthUser");
        removeUser("seventhUser");
        removeUser("eighthUser");
        removeUser("ninthUser");
        removeUser("tenthUser");
        removeUser("eleventhUser");
        removeUser("twelfthUser");
        return "Done.";

    }

    public String addGroup(GroupRequest groupRequest) {
        String teamName = groupRequest.getTeamname();
        String userName = groupRequest.getUsername();

        try {
            User user = accountRepository.findByUsername(userName);
            String companyName = user.getCompanyName();

            if (runnerGroupRepository.existsByTeamName(teamName)) {
                return "Group name already exists";
            }

            runnerGroupService.createRunnerGroup(teamName, user, companyName);
            
            accountRepository.save(user);
            return "Success";
        } catch (Exception e) {
            e.printStackTrace();
            return "ERROR: " + e;
        }
    }

    public String addUserToGroup(AddUserToGroupRequest addUserToGroupRequest) {
        String inviteCode = addUserToGroupRequest.getInviteCode();
        String username = addUserToGroupRequest.getUsername();

            RunnerGroup runnerGroup = runnerGroupRepository.findGroupByInviteCode(inviteCode);
            User user = accountRepository.findByUsername(username);

            if (runnerGroup == null) {
                return "Group with invite code " + inviteCode + " not found";
            }

            if (user.getRunnerGroup() != null) {
                return "Already in a group!";
            }

            if (runnerGroup.isFull()) {
                return "Group is full!";
            }
            runnerGroup.addUser(user);
            runnerGroupRepository.save(runnerGroup);
            accountRepository.save(user);
            return "Success!";
    }
    

    public boolean removeUser(String username) {
        User user = accountRepository.findByUsername(username);
        if (user == null) {
            return false;
        }

        List<Run> runs = runRepository.getAllRunsByUser(username);

        for (Run run : runs) {
            runRepository.delete(run);
        }
        
        accountRepository.delete(user);
        
        if (user.getRunnerGroup() != null) {
            RunnerGroup runnerGroup = user.getRunnerGroup();
            runnerGroup.getUsers().remove(user);
            if (runnerGroup.getUsers().isEmpty()){
                runnerGroupRepository.delete(runnerGroup);
            } else {
                runnerGroupRepository.save(runnerGroup);
            }
        }

        return true;
    }


}
