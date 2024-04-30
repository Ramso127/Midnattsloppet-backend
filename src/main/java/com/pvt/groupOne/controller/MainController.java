package com.pvt.groupOne.controller;

import com.pvt.groupOne.model.*;
import com.pvt.groupOne.Service.StravaService;
import com.pvt.groupOne.Service.UserService;
import jakarta.servlet.http.HttpServletRequest;
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
import com.pvt.groupOne.repository.StravaUserRepository;
import com.pvt.groupOne.repository.AccountRepository;

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

    @GetMapping(value = "/hello")
    public @ResponseBody String testMethod() {
        return "Hello this is Didrik's test";
    }

    @GetMapping(value = "/greet/{firstName}/{lastName}")
    public @ResponseBody String greetUser(@PathVariable String firstName, @PathVariable String lastName) {
        return "Hello, " + firstName + " " + lastName + "!";
    }

    @PostMapping(value = "/adduser")
    public @ResponseBody String addUser(@RequestBody UserRequest userRequest) {
        try {
            String username = userRequest.getUsername();
            String password = userRequest.getPassword();
            if (accountRepository.existsByUsername(username))
                return "Username already exists";
            User newUser = new User();
            newUser.setUserName(username);
            newUser.setPassword(password);
            accountRepository.save(newUser);

            return "User " + username + " with password " + password + " has been added to the database.";
        } catch (Exception e) {
            return e.toString();
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
                return ResponseEntity.ok("Login successful");
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.toString());
        }
    }

    @GetMapping(value = "/addgroup/{groupName}/{groupType}")
    public @ResponseBody String addGroup(@PathVariable String groupName, @PathVariable String groupType) {
        if (groupRepository.existsByGroupName(groupName))
            return "Groupname already exists";
        RunnerGroup newGroup = new RunnerGroup();
        newGroup.setGroupName(groupName);
        newGroup.setGroupType(groupType);

        try {
            groupRepository.save(newGroup);

        } catch (Exception e) {
            return "Error: " + e;
        }

        return groupName + " of type " + groupType + " has been added to the database.";
    }

    @DeleteMapping(value = "/removeGroup")
    public @ResponseBody String removeGroup(@RequestBody RunnerGroup group) {
        if (!groupRepository.existsByGroupName(group.getGroupName())) {
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

    // TODO Make this return a boolean when everything works 100%
    @GetMapping("/exchange_token")
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
            StravaService myExchanger = new StravaService();

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

        // String firstName = newUser.getFirstName();
        // return "SUCCESS! User " + firstName + " authorized.";
    }

}