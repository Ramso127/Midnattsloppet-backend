package com.pvt.groupOne.controller;

import com.pvt.groupOne.model.*;
import com.pvt.groupOne.Service.StravaService;
import com.pvt.groupOne.Service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pvt.groupOne.Service.RunnerGroupService;
import java.util.ArrayList;

import com.pvt.groupOne.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(path = "/controller")
@CrossOrigin
public class MainController {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private RunnerGroupRepository groupRepository;

    @Autowired
    private StravaUserRepository stravaUserRepository;

    @Autowired
    private StravaRunRepository stravaRunRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private RunnerGroupService runnerGroupService;

    @GetMapping(value = "/hello")
    public @ResponseBody String testMethod() {
        return "Hello this is Didrik's test";
    }

    @PostMapping(value = "/adduser", produces = "application/json")
    public @ResponseBody String addUser(@RequestBody UserRequest userRequest) {
        try {
            String username = userRequest.getUsername();
            String password = userRequest.getPassword();
            String email = userRequest.getEmail();
            String companyName = userRequest.getCompanyname();
            if (accountRepository.existsByUsername(username))
                return "{\"message\": \"Username already exists\"}";

            if (accountRepository.existsByEmail(email))
                return "{\"message\": \"Email already exists\"}";

            User newUser = new User();
            newUser.setUserName(username);
            newUser.setPassword(password);
            newUser.setEmail(email);
            newUser.setCompanyName(companyName);
            accountRepository.save(newUser);
            ObjectMapper om = new ObjectMapper();
            return om.writeValueAsString(newUser);
        } catch (Exception e) {
            return "{\"error\": \"" + e.toString() + "\"}";
        }
    }

    @DeleteMapping(value = "/removeuser") // eller "/removeuser/{userId}"
    public @ResponseBody String removeUser(@RequestBody User user) {
        if (!accountRepository.existsByUsername(user.getUserName())) {
            return "No such user exists";
        }
        accountRepository.delete(user);
        return "The user has been removed";
    }

    // Gör om till PostMapping
    @GetMapping(value = "/login/{username}/{password}")
    public ResponseEntity<String> login(@PathVariable String username, @PathVariable String password) {
        // Perform user authentication

        try {
            if (userService.authenticateUser(username, password)) {
                User user = accountRepository.findByUsername(username);
                ObjectMapper om = new ObjectMapper();
                return ResponseEntity.ok(om.writeValueAsString(user));
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.toString());
        }
    }

    @PostMapping(value = "/addgroup", produces = "application/json")
    public @ResponseBody String addGroup(@RequestBody GroupRequest groupRequest) {
        String teamName = groupRequest.getTeamname();
        String userName = groupRequest.getUsername();
        try {
            User user = accountRepository.findByUsername(userName);

            if (groupRepository.existsByTeamName(teamName)) {
                return "{\"message\": \"Group name already exists\"}";
            }
            RunnerGroup runnerGroup = runnerGroupService.createRunnerGroup(teamName, user);
            accountRepository.save(user);
            ObjectMapper om = new ObjectMapper();
            return om.writeValueAsString(runnerGroup);
        } catch (Exception e) {
            e.printStackTrace();
            return "ERROR: " + e;
        }
    }

    @PostMapping(value = "/addusertogroup")
    public @ResponseBody String addUserToGroup(@RequestBody String username, @RequestParam String inviteCode) {
        RunnerGroup runnerGroup = groupRepository.findGroupByInviteCode(inviteCode);
        User user = accountRepository.findByUsername(username);

        if (runnerGroup == null) {
            return "Group with invite code " + inviteCode + " not found";
        }

        if (user.getRunnerGroup() != null) {
            return "Already in a group";
        }

        if (runnerGroup.isFull()) {
            return "Group is full";
        }
        runnerGroup.addUser(user);
        groupRepository.save(runnerGroup);
        accountRepository.save(user);
        return user.getUserName() + " added to " + runnerGroup.getTeamName();
    }

    @DeleteMapping(value = "/removeGroup")
    public @ResponseBody String removeGroup(@RequestBody RunnerGroup group) {
        if (!groupRepository.existsByTeamName(group.getTeamName())) {
            return "No such group exists";
        }
        groupRepository.delete(group);
        return "The group has been removed";
    }

    // TODO kolla om användarnamn eller epost finns redan som en GET mapping

    @GetMapping(value = "/checkusername/{username}")
    public @ResponseBody Boolean checkUsernameExistsAlready(@PathVariable String username) {
        if (accountRepository.existsByUsername(username))
            return true;
        else
            return false;
    }

    @GetMapping(value = "/checkemail/{email}")
    public @ResponseBody Boolean checkEmailExistsAlready(@PathVariable String email) {
        if (accountRepository.existsByUsername(email))
            return true;
        else
            return false;
    }

    // TODO DIDDE change return statements
    // TODO Ändra till PostMapping? Behöver något mer ändras än så?
    @GetMapping("/saveauthenticateduser/{username}")
    public @ResponseBody String saveStravaToken(@PathVariable String username,
            @RequestParam(required = false) String error,
            @RequestParam("code") String authCode,
            @RequestParam("scope") String scope) {

        if (error != null && error.equals("access_denied")) {
            System.out.println("Access denied");
            return "ERROR, Access denied";
        }

        if (!scope.contains("activity:read")) {
            return "ERROR: User must accept activity:read";
        }

        try {
            StravaService myExchanger = new StravaService(stravaUserRepository);

            StravaUser stravaUser = myExchanger.exchangeToken(authCode);
            stravaUser.setScope(scope);

            if (accountRepository.findByUsername(username) == null) {
                return "ERROR: username not found";
            }

            User newUser = accountRepository.findByUsername(username);
            stravaUser.setUser(newUser);

            String result = "ID: " + stravaUser.getId() + "\nName: " + stravaUser.getFirstName() + "\n Scope: "
                    + stravaUser.getScope() + "\nAccess token: " + stravaUser.getAccessToken() + "\nRefresh token: "
                    + stravaUser.getRefreshToken() + "\nExpires at: " + stravaUser.getExpiresAt();

            stravaUserRepository.save(stravaUser);
            return "Strava account with this information successfully connected: " + result;

        } catch (Exception e) {
            return "Error: " + e;
        }

    }

    // TODO DIDDE Gör om till PostMapping?
    @GetMapping(value = "/saverunsfrom/{username}")
    public @ResponseBody String saveRunsFrom(@PathVariable String username) {
        StravaUser stravaUser = stravaUserRepository.findByUsername(username);
        int stravaID = stravaUser.getId();
        StravaService myService = new StravaService(stravaUserRepository);
        String accessToken = stravaUser.getAccessToken();
        long currentSystemTime = System.currentTimeMillis() / 1000L;

        // If the access token has expired,
        // request a new one and add it to the database
        if (stravaUser.getExpiresAt() < currentSystemTime) {
            boolean result = myService.requestNewTokens(stravaUser.getRefreshToken(), stravaID);
            if (result) {
                System.out.println("New token successfully fetched");
            } else {
                return "Error: token has not been updated";
            }
        }

        long latestFetch = stravaUser.getTimeOfLatestFetchUNIX();

        ArrayList<StravaRun> runList = myService.saveRunsFrom(stravaID, latestFetch, accessToken);
        for (StravaRun run : runList) {
            stravaRunRepository.save(run);
        }
        stravaUser.setTimeOfLatestFetchUNIX(currentSystemTime);
        return "Done";

    }

}