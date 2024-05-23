package com.pvt.groupOne.controller;

import com.pvt.groupOne.model.*;
import com.pvt.groupOne.Service.StravaService;
import com.pvt.groupOne.Service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pvt.groupOne.Service.RunService;
import com.pvt.groupOne.Service.RunnerGroupService;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.WeekFields;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

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
    private UserImageRepository userImageRepository;

    @Autowired
    private BugReportRepository bugReportRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private RunnerGroupService runnerGroupService;

    @Autowired
    private RunService runService;

    @Autowired
    private RunRepository runRepository;

    @Autowired
    GroupImageRepository groupImageRepository;

    @Autowired
    private PasswordEncryption passwordEncryption;

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

            User newUser = new User(username, passwordEncryption.passwordEncoder().encode(password), email,
                    companyName);

            accountRepository.save(newUser);
            ObjectMapper om = new ObjectMapper();
            return ResponseEntity.status(HttpStatus.CREATED).body(om.writeValueAsString(newUser));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\": \"" + e.toString() + "\"}");
        }
    }

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
            String companyName = user.getCompanyName();

            if (groupRepository.existsByTeamName(teamName)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("{\"message\": \"Group name already exists\"}");
            }

            RunnerGroup runnerGroup = runnerGroupService.createRunnerGroup(teamName, user, companyName);
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

            if (user == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("{\"message\": \"User not found\"}");
            }

            if (runnerGroup == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("{\"message\": \"Group with invite code " + inviteCode + " not found\"}");
            }

            if (user.getRunnerGroup() != null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"message\": \"Already in a group!\"}");
            }

            String groupCompanyNormalized = runnerGroup.getCompanyName().replaceAll("\\s+", "");
            String userCompanyNormalized = user.getCompanyName().replaceAll("\\s+", "");

            if (!groupCompanyNormalized.equalsIgnoreCase(userCompanyNormalized)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("{\"message\": \"Group belongs to a different company!\"}");
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

    // This needs to be GET-mapping for Strava's redirect URI
    @GetMapping("/saveauthenticateduser/{username}")
    public @ResponseBody String saveStravaToken(
            @RequestParam(required = false) String error,
            @RequestParam("code") String authCode,
            @RequestParam("scope") String scope,
            @PathVariable("username") String username) {

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
                    + ". You can now close this page and return to the app.";

        } catch (Exception e) {
            return "Error: " + e;
        }

    }

    @PutMapping(value = "/fetchruns")
    public ResponseEntity<String> fetchRuns(@RequestParam("username") String username) {
        StravaUser stravaUser = stravaUserRepository.findByUser_Username(username);

        if (stravaUser == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("ERROR: No Strava account connected to user " + username);
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
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Error: token has not been updated");
            }
        }

        long latestFetch = stravaUser.getTimeOfLatestFetchUNIX();

        ArrayList<Run> runList = myService.saveRunsFrom(stravaID, latestFetch, accessToken, user);
        if (runList.isEmpty()) {
            Date myDate = new Date();
            myDate.setTime(latestFetch * 1000L);
            Instant instant = myDate.toInstant();

            // Change timezone to Sweden
            ZonedDateTime zonedDateTime = instant.atZone(ZoneId.of("Europe/Stockholm"));

            LocalDate date = zonedDateTime.toLocalDate();
            DayOfWeek dayOfWeek = zonedDateTime.getDayOfWeek();
            LocalTime time = zonedDateTime.toLocalTime();
            ZoneId timezone = zonedDateTime.getZone();
            ZoneOffset offset = zonedDateTime.getOffset();

            String dayString = dayOfWeek.toString();
            dayString = dayString.toLowerCase();

            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
                    .body("ERROR: No new runs available since " + dayString
                            + " " + date + " at " + time + " (" + timezone + " " + offset + ")");
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
        return ResponseEntity.ok("Done. " + counter + runWord + " added.");

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
        // Parse the formatted date string back into a LocalDate object
        LocalDate formattedLocalDate;
        String formattedTime;
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

            // Assuming runRequest.getDate() returns a LocalDate object
            String date = runRequest.getDate();
            LocalDate localDate = LocalDate.parse(date, formatter);

            // Format the LocalDate object using the formatter
            String formattedDate = localDate.format(formatter);

            formattedLocalDate = LocalDate.parse(formattedDate, formatter);

            int minutes = runRequest.getMinutes();
            int hours = minutes / 60;
            minutes = minutes % 60;

            int seconds = runRequest.getSeconds();

            formattedTime = String.format("%02d:%02d:%02d", hours, minutes, seconds);
        } catch (DateTimeParseException e) {
            return ResponseEntity.badRequest().body("{\"error\":\"Invalid date format\"}");
        }
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

            BigDecimal bd = new BigDecimal(Double.toString(totalDistance));
            bd = bd.setScale(2, RoundingMode.HALF_UP);
            double roundedValue = bd.doubleValue();

            Map<String, Object> userMap = new HashMap<>();
            userMap.put("username", username);
            userMap.put("distance", roundedValue);
            jsonList.add(userMap);
        }

        jsonList.sort((map1, map2) -> {
            double distance1 = (double) map1.get("distance");
            double distance2 = (double) map2.get("distance");
            return Double.compare(distance2, distance1);
        });

        response.put("data", jsonList);
        return response;
    }

    @GetMapping(value = "/gettop3")
    public @ResponseBody String getTop3() {
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

        List<Run> runs = runRepository.getAllRunsByUser(username);

        for (Run run : runs) {
            runRepository.delete(run);
        }

        UserImage image = userImageRepository.findByUserName(username);

        if (image != null) {

            userImageRepository.delete(image);
        }

        accountRepository.delete(user);

        if (user.getRunnerGroup() != null) {
            RunnerGroup runnerGroup = user.getRunnerGroup();
            runnerGroup.getUsers().remove(user);
            if (runnerGroup.getUsers().isEmpty()) {
                removeGroup(runnerGroup.getTeamName());
            } else {
                groupRepository.save(runnerGroup);
            }
        }

        return "User " + username + " has been removed.";
    }

    @DeleteMapping(value = "/remove-user-from-group/{username}")
    public ResponseEntity<String> removeUserFromTeam(@PathVariable String username) {
        User user = accountRepository.findByUsername(username);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"message\": \"User not found!\"}");
        }

        if (user.getRunnerGroup() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("{\"message\": \"User is not connected to a group!\"}");
        }

        RunnerGroup runnerGroup = user.getRunnerGroup();
        runnerGroup.getUsers().remove(user);

        user.setRunnerGroup(null);
        accountRepository.save(user);

        if (runnerGroup.getUsers().isEmpty()) {
            removeGroup(runnerGroup.getTeamName());
        } else {
            runnerGroup.setFull(false);
            groupRepository.save(runnerGroup);
        }

        return ResponseEntity.ok(
                "{\"message\": \"User " + username + " has been removed from team " + runnerGroup.getTeamName() + "}");
    }

    @DeleteMapping(value = "/remove-group/{groupname}")
    public ResponseEntity<String> removeGroup(@PathVariable String groupname) {

        RunnerGroup group = groupRepository.findGroupByTeamName(groupname);

        if (group == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"message\": \"Group not found!\"}");
        }

        List<User> users = group.getUsers();

        for (User user : users) {
            user.setRunnerGroup(null);
        }

        GroupImage groupImage = groupImageRepository.findByGroupName(group.getTeamName());

        if (groupImage != null) {
            groupImageRepository.delete(groupImage);

        }

        groupRepository.delete(group);
        return ResponseEntity.ok("{\"message\": \"Team " + groupname + " has been removed}");

    }

    @GetMapping(value = "/check-strava-user/{username}")
    public ResponseEntity<String> checkStravaUser(@PathVariable String username) {
        StravaUser stravaUser = stravaUserRepository.findByUser_Username(username);

        if (stravaUser == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("{\"message\": \"Strava user is not connected\"}");

        }

        return ResponseEntity.ok("{\"message\": \"Strava user is connected\"}");

    }

    // TODO NOA Gör om till PUT (uppdatering)
    @PostMapping(value = "/re-encrypt-password/{username}/{newPassword}")
    public ResponseEntity<String> reEncryptPassword(@PathVariable String username, @PathVariable String newPassword) {
        User user = accountRepository.findByUsername(username);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"message\": \"User not found!\"}");
        }

        user.setPassword(passwordEncryption.passwordEncoder().encode(newPassword));
        accountRepository.save(user);

        return ResponseEntity.ok("{\"message\": \"Password has been re-encrypted\"}");
    }

    @PutMapping(value = "/update-company")
    public ResponseEntity<String> updateCompany(@RequestBody CompanyRequest companyRequest) {
        String username = companyRequest.getUsername();
        String newCompany = companyRequest.getCompanyName();

        User user = accountRepository.findByUsername(username);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"message\": \"User not found!\"}");
        }

        // Delete user from group
        if (user.getRunnerGroup() != null) {
            removeUserFromTeam(username);
        }

        user.setCompanyName(newCompany);
        accountRepository.save(user);

        return ResponseEntity.ok("{\"message\": \"Company has been changed\"}");
    }

    @PostMapping(value = "/save-bug-report")
    public ResponseEntity<String> saveBugReport(@RequestParam String report) {

        if (report.length() > 100) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"message\": \"Message is too long!\"}");
        }

        BugReport myReport = new BugReport(report);

        bugReportRepository.save(myReport);

        return ResponseEntity.status(HttpStatus.OK).body("{\"message\": \"Bug report successfully saved!\"}");
    }

    @GetMapping(value = "/get-current-round")
    public ResponseEntity<String> getRoundByCurrentWeek() {

        int result;

        ZoneId stockholmZone = ZoneId.of("Europe/Stockholm");
        LocalDate date = ZonedDateTime.now(stockholmZone).toLocalDate();

        WeekFields weekFields = WeekFields.of(Locale.getDefault());

        int weekNumber = date.get(weekFields.weekOfWeekBasedYear());

        switch (weekNumber) {

            case 15:
                result = 1;
                break;
            case 16:
                result = 2;
                break;

            case 17:
                result = 3;
                break;

            case 18:
                result = 4;
                break;

            case 19:
                result = 5;
                break;

            case 20:
                result = 6;
                break;

            case 21:
                result = 7;
                break;

            case 22:
                result = 8;
                break;

            case 23:
                result = 9;
                break;

            case 24:
                result = 10;
                break;

            case 25:
                result = 11;
                break;

            case 26:
                result = 12;
                break;

            case 27:
                result = 13;
                break;

            case 28:
                result = 14;
                break;

            case 29:
                result = 15;
                break;

            case 30:
                result = 16;
                break;

            case 31:
                result = 17;
                break;

            case 32:
                result = 18;
                break;

            default:
                result = -1;

        }

        if (result < 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("{\"message\":\"There is no competition right now.\"}");
        } else {
            return ResponseEntity.status(HttpStatus.OK).body(String.valueOf(result));
        }
    }

}
