package com.pvt.groupOne.controller;

import com.pvt.groupOne.model.*;
import com.pvt.groupOne.Service.StravaService;
import com.pvt.groupOne.Service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pvt.groupOne.Service.RunnerGroupService;
import java.util.ArrayList;

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

import com.pvt.groupOne.repository.RunnerGroupRepository;
import com.pvt.groupOne.repository.StravaRunRepository;
import com.pvt.groupOne.repository.StravaUserRepository;
import com.pvt.groupOne.repository.AccountRepository;
import com.pvt.groupOne.repository.ImageRepository;

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

    @Autowired
    private ImageRepository imageRepository;

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

    @PostMapping(value = "/addgroup")
    public @ResponseBody String addGroup(@RequestBody GroupRequest groupRequest) {
        String teamName = groupRequest.getTeamName();
        String username = groupRequest.getUser();
        try {
            User user = accountRepository.findByUsername(username);

            if (groupRepository.existsByTeamName(teamName)) {
                return "Groupname already exists";
            }
            runnerGroupService.createRunnerGroup(teamName, user);

            accountRepository.save(user);
            return teamName + " and user " + username + " has been added to the database.";

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

    // TODO DIDDE Make this return a boolean when everything works 100%
    @GetMapping("/saveauthenticateduser")
    public @ResponseBody String saveStravaToken(@RequestParam(required = false) String error,
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

            StravaUser newUser = myExchanger.exchangeToken(authCode);
            newUser.setScope(scope);

            String result = "ID: " + newUser.getId() + "\nName: " + newUser.getFirstName() + "\n Scope: "
                    + newUser.getScope() + "\nAccess token: " + newUser.getAccessToken() + "\nRefresh token: "
                    + newUser.getRefreshToken() + "\nExpires at: " + newUser.getExpiresAt();

            stravaUserRepository.save(newUser);
            return result;

        } catch (Exception e) {
            return "Error: " + e;
        }

    }

    @GetMapping(value = "/saverunsfrom/{stravaID}/{unixTime}")
    public @ResponseBody String saveRunsFrom(@PathVariable int stravaID, @PathVariable int unixTime) {
        StravaUser myUser = stravaUserRepository.findById(stravaID);
        StravaService myService = new StravaService(stravaUserRepository);
        String accessToken = myUser.getAccessToken();
        long currentSystemTime = System.currentTimeMillis() / 1000L;

        // If the access token has expired,
        // request a new one and add it to the database
        if (myUser.getExpiresAt() < currentSystemTime) {
            boolean result = myService.requestNewTokens(myUser.getRefreshToken(), stravaID);
            if (result) {
                System.out.println("New token successfully fetched");
            } else {
                return "Error: token has not been updated";
            }
        }

        ArrayList<StravaRun> runList = myService.saveRunsFrom(stravaID, unixTime, accessToken);
        for (StravaRun run : runList) {
            stravaRunRepository.save(run);
        }
        return "Done";

    }

    @PostMapping(value = "/addimage")
    public @ResponseBody String addImage(@RequestParam String username, @RequestParam String base64image) {
        if (imageRepository.findByuserName(username) != null) {
            return "ERROR: Image already exists for user.";
        }

        Image myImage = new Image(username, base64image);
        imageRepository.save(myImage);
        return "Image for " + username + " successfully saved.";
    }

}