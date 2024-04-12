package com.pvt.groupOne.controller;

import com.pvt.groupOne.model.User;
import com.pvt.groupOne.model.UserInfo;
import com.pvt.groupOne.Service.UserService;
import com.pvt.groupOne.model.RunnerGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.pvt.groupOne.repository.RunnerGroupRepository;
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
    private UserService userService;

    @GetMapping(value = "/hello")
    public @ResponseBody String testMethod() {
        return "Hello this is Didrik's test";
    }

    @GetMapping(value = "/greet/{firstName}/{lastName}")
    public @ResponseBody String greetUser(@PathVariable String firstName, @PathVariable String lastName) {
        return "Hello, " + firstName + " " + lastName + "!";
    }

    @GetMapping(value = "/adduser/{username}/{password}/{email}")
    public @ResponseBody String addUser(@PathVariable String username, @PathVariable String password,
            @PathVariable String email) {
        if (accountRepository.existsByUsername(username))
            return "Username already exists";
        User newUser = new User();
        newUser.setUserName(username);
        newUser.setPassword(password);
        newUser.setEmail(email);
        accountRepository.save(newUser);

        return "User " + username + " with password " + password + " has been added to the database.";
    }

    @GetMapping(value = "/login/{username}/{password}")
    public ResponseEntity<String> login(@PathVariable String username, @PathVariable String password) {
        // Perform user authentication
        if (userService.authenticateUser(username, password)) {
            return ResponseEntity.ok("Login successful");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
        }
    }

    @GetMapping(value = "/addgroup/{groupName}/{groupType}")
    public @ResponseBody String addGroup(@PathVariable String groupName, @PathVariable String groupType) {
        if (groupRepository.existsByGroupName(groupName))
            return "Groupname already exists";
        RunnerGroup newGroup = new RunnerGroup();
        newGroup.setGroupName(groupName);
        newGroup.setGroupType(groupType);
        groupRepository.save(newGroup);

        return groupName + " of type " + groupType + " has been added to the database.";
    }

}