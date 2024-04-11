package com.pvt.groupOne.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import com.pvt.groupOne.model.RunnerGroup;
import com.pvt.groupOne.repository.RunnerGroupRepository;
import com.pvt.groupOne.model.User;
import com.pvt.groupOne.repository.UserRepository;

@Controller
@RequestMapping(path = "/controller")
@CrossOrigin
public class MainController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RunnerGroupRepository groupRepository;

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
        User newUser = new User();
        newUser.setUserName(username);
        newUser.setPassword(password);
        newUser.setEmail(email);
        userRepository.save(newUser);

        return "User " + username + " with password " + password + " has been added to the database.";
    }

    @GetMapping(value = "/addgroup/{groupName}/{groupType}")
    public @ResponseBody String addGroup(@PathVariable String groupName, @PathVariable String groupType) {
        RunnerGroup newGroup = new RunnerGroup();
        newGroup.setGroupName(groupName);
        newGroup.setGroupType(groupType);
        groupRepository.save(newGroup);

        return groupName + " of type " + groupType + " has been added to the database.";
    }

    @PostMapping(value = "/addTest/{groupName}/{groupType}")
    public @ResponseBody String addTest(@PathVariable String groupName, @PathVariable String groupType) {
        RunnerGroup newGroup = new RunnerGroup();
        newGroup.setGroupName(groupName);
        newGroup.setGroupType(groupType);
        groupRepository.save(newGroup);

        return groupName + " of type " + groupType + " has been added to the database. ";
    }


}