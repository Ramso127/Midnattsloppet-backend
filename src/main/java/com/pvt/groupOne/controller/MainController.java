package com.pvt.groupOne.controller;

import com.pvt.groupOne.model.*;
import com.pvt.groupOne.Service.StravaService;
import com.pvt.groupOne.Service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pvt.groupOne.Service.RunService;
import com.pvt.groupOne.Service.RunnerGroupService;
import java.time.format.DateTimeFormatter;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.pvt.groupOne.repository.*;
import jakarta.persistence.OneToOne;
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
import org.springframework.web.bind.annotation.PutMapping;
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
    private UserService userService;

    @Autowired
    private RunnerGroupService runnerGroupService;

    @Autowired
    private RunService runService;

    @Autowired
    private RunRepository runRepository;

    @GetMapping(value = "/hello")
    public @ResponseBody String testMethod() {
        return "Hello this is Didrik's test";
    }

    @PostMapping(value = "/adduser", produces = "application/json")
    public @ResponseBody ResponseEntity<String> addUser(@RequestBody UserRequest userRequest) {
        try {
            String username = userRequest.getUsername();
            String password = userRequest.getPassword();
            String email = userRequest.getEmail();
            String companyName = userRequest.getCompanyname();
            if (accountRepository.existsByUsername(username))
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"message\": \"Username already exists\"}");

            if (accountRepository.existsByEmail(email))
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"message\": \"Email already exists\"}");

            User newUser = new User(username, password, email, companyName);

            accountRepository.save(newUser);
            ObjectMapper om = new ObjectMapper();
            return ResponseEntity.status(HttpStatus.CREATED).body(om.writeValueAsString(newUser));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\": \"" + e.toString() + "\"}");
        }
    }

    @DeleteMapping(value = "/removeuser") // eller "/removeuser/{userId}"
    public @ResponseBody String removeUser(@RequestBody User user) {
        String username = user.getUsername();
        if (!accountRepository.existsByUsername(username)) {
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
    public ResponseEntity<String> addGroup(@RequestBody GroupRequest groupRequest) {
        String teamName = groupRequest.getTeamname();
        String userName = groupRequest.getUsername();
        try {
            User user = accountRepository.findByUsername(userName);

            if (groupRepository.existsByTeamName(teamName)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("{\"message\": \"Group name already exists\"}");
            }

            RunnerGroup runnerGroup = runnerGroupService.createRunnerGroup(teamName, user);
            accountRepository.save(user);
            ObjectMapper om = new ObjectMapper();
            return ResponseEntity.status(HttpStatus.CREATED).body(om.writeValueAsString(runnerGroup));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("ERROR: " + e);
        }
    }

    @PostMapping(value = "/addusertogroup")
    public ResponseEntity<String> addUserToGroup(@RequestBody AddUserToGroupRequest addUserToGroupRequest) {
        String inviteCode = addUserToGroupRequest.getInviteCode();
        String username = addUserToGroupRequest.getUsername();
        try {
            RunnerGroup runnerGroup = groupRepository.findGroupByInviteCode(inviteCode);
            User user = accountRepository.findByUsername(username);

            if (runnerGroup == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("{\"message\": \"Group with invite code " + inviteCode + " not found\"}");
            }

            if (user.getRunnerGroup() != null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"message\": \"Already in a group!\"}");
            }

            if (runnerGroup.isFull()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"message\": \"Group is full!\"}");
            }
            runnerGroup.addUser(user);
            groupRepository.save(runnerGroup);
            accountRepository.save(user);
            ObjectMapper om = new ObjectMapper();
            return ResponseEntity.ok(om.writeValueAsString(runnerGroup));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.toString());
        }
    }

    @DeleteMapping(value = "/removeGroup")
    public @ResponseBody String removeGroup(@RequestBody RunnerGroup group) {
        if (!groupRepository.existsByTeamName(group.getTeamName())) {
            return "No such group exists";
        }
        groupRepository.delete(group);
        return "The group has been removed";
    }

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
    @GetMapping("/saveauthenticateduser/{username}")
    public @ResponseBody String saveStravaToken(
            @RequestParam(required = false) String error,
            @RequestParam("code") String authCode,
            @RequestParam("scope") String scope,
            @PathVariable("username") String username) {

        // TODO DIDDE App crashes when trying to authenticate someone who's already
        // authenticated

        StravaUser myUser = stravaUserRepository.findByUser_Username(username);

        boolean isUserConnected = myUser != null && myUser.getUser().getUsername().equals(username);

        if (isUserConnected) {
            return "ERROR: user " + username + " is already connected to this Strava account.";
        }

        if (myUser != null) {
            return "ERROR: user " + username + " already has a connected Strava account.";
        }

        boolean isAccessDenied = error != null && error.equals("access_denied");

        if (isAccessDenied) {
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

            stravaUserRepository.save(stravaUser);
            return "Success! Thank you " + stravaUser.getFirstName()
                    + ". You can now close this page and return to the app";

        } catch (Exception e) {
            return "Error: " + e;
        }

    }

    @PutMapping(value = "/fetchruns")
    public @ResponseBody String fetchRuns(@RequestParam("username") String username) {
        StravaUser stravaUser = stravaUserRepository.findByUser_Username(username);

        if (stravaUser == null) {
            return "ERROR: User " + username + " not found.";
        }
        User user = accountRepository.findByUsername(username);
        int stravaID = stravaUser.getId();
        StravaService myService = new StravaService(stravaUserRepository);
        String accessToken = stravaUser.getAccessToken();
        long currentSystemTime = System.currentTimeMillis() / 1000L;
        int counter = 0;
        String runWord = " run";

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

        ArrayList<Run> runList = myService.saveRunsFrom(stravaID, latestFetch, accessToken, user);
        if (runList.isEmpty()) {
            Date myDate = new Date();
            myDate.setTime(latestFetch * 1000L);
            return "ERROR: No new runs available since: \n" + myDate.toString();
        }

        for (Run run : runList) {
            runService.saveRun(run);
            counter++;
        }
        stravaUser.setTimeOfLatestFetchUNIX(currentSystemTime);
        stravaUserRepository.save(stravaUser);

        if (counter > 1) {
            runWord += "s";
        }
        return "Done. " + counter + runWord + " added.";

    }

    @PostMapping(value = "/addrun", produces = "application/json")
    public ResponseEntity<?> addRun(@RequestBody RunRequest runRequest) {
        // Check if the user exists
        String username = runRequest.getUsername();
        User user = accountRepository.findByUsername(username);
        if (user == null) {
            return ResponseEntity.badRequest().body("{\"error\":\"User does not exist\"}");
        }

        // Define the desired date format
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        // Assuming runRequest.getDate() returns a LocalDate object
        String date = runRequest.getDate();
        LocalDate localDate = LocalDate.parse(date, formatter);

        // Format the LocalDate object using the formatter
        String formattedDate = localDate.format(formatter);

        // Parse the formatted date string back into a LocalDate object
        LocalDate formattedLocalDate = LocalDate.parse(formattedDate, formatter);

        int minutes = runRequest.getMinutes();
        int hours = minutes / 60;

        minutes = minutes % 60;

        int seconds = runRequest.getSeconds();

        String formattedTime = String.format("%02d:%02d:%02d", hours, minutes, seconds);
        // Create and save the new run
        double totaldistance = runRequest.getTotaldistance();
        Run newRun = new Run(formattedLocalDate, totaldistance, formattedTime, user);

        runService.saveRun(newRun);

        return ResponseEntity.ok(newRun);
    }

    @GetMapping(value = "/getNumberOfTeams")
    public @ResponseBody String getNumberOfTeams() {
        int numberOfTeams = groupRepository.countDistinctTeams();
        String response = "{\"numberOfTeams\": \"" + numberOfTeams + "\"}";
        return response;
    }

    @GetMapping(value = "/getteammembers/{groupname}")
    public @ResponseBody Map<String, List<Map<String, Object>>> getTeamMembers(@PathVariable String groupname) {
        RunnerGroup runnerGroup = groupRepository.findGroupByTeamName(groupname);
        Map<String, List<Map<String, Object>>> response = new HashMap<>();
        List<User> list = runnerGroup.getUsers();
        List<Map<String, Object>> jsonList = new ArrayList<>();
        for (User user : list) {
            String username = user.getUsername();
            List<Double> runDistanceList = runRepository.getAllRunDistanceByUser(username);
            double totalDistance = 0;
            for (double distance : runDistanceList) {
                totalDistance += distance;
            }
            Map<String, Object> userMap = new HashMap<>();
            userMap.put("username", username);
            userMap.put("distance", totalDistance);
            jsonList.add(userMap);
        }
        response.put("data", jsonList);
        return response;
    }

    @GetMapping(value = "/gettop3")
    public @ResponseBody String getTeamMembers() {
        ObjectMapper om = new ObjectMapper();
        try {
            List<Object[]> top3GroupsByTotalDistance = groupRepository.findTop3GroupsByTotalDistance();
            return om.writeValueAsString(top3GroupsByTotalDistance);
        } catch (JsonProcessingException e) {
            return e.toString();
        }
    }

    @DeleteMapping(value = "/removeuser/{username}")
    public @ResponseBody String removeUser(@PathVariable String username) {
        User user = accountRepository.findByUsername(username);
        if (user == null) {
            return "ERROR: User " + username + " not found.";
        }
        if(user.getRunnerGroup()!= null) {
            RunnerGroup runnerGroup = user.getRunnerGroup();
            runnerGroup.getUsers().remove(user);
            groupRepository.save(runnerGroup);
        }

        accountRepository.delete(user);
        return "User " + username + " has been removed.";
    }

    @DeleteMapping(value = "/remove-user-from-group/{username}")
    public ResponseEntity<String> removeUserFromTeam(@PathVariable String username) {
        User user = accountRepository.findByUsername(username);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"message\": \"User not found!\"}");
        }

        if(user.getRunnerGroup() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"message\": \"User is not connected to a group!\"}");
        }

        RunnerGroup runnerGroup = user.getRunnerGroup();
        runnerGroup.getUsers().remove(user);
        user.setRunnerGroup(null);
        groupRepository.save(runnerGroup);
        accountRepository.save(user);

        return ResponseEntity.ok("User " + username + " has been removed from team " + runnerGroup.getTeamName());
    }

}
