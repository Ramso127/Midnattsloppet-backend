package com.pvt.groupOne.controller;

import com.pvt.groupOne.model.User;
import com.pvt.groupOne.model.RunnerGroup;

import java.net.URI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.pvt.groupOne.repository.RunnerGroupRepository;

import jakarta.servlet.http.HttpServletRequest;

import com.pvt.groupOne.repository.AccountRepository;

@Controller
@RequestMapping(path = "/controller")
@CrossOrigin
public class MainController {

    @Autowired
    private AccountRepository accountRepository;

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

    @PostMapping(value = "/adduser/{username}/{password}/{email}")
    public @ResponseBody ResponseEntity<User> addUser(@PathVariable String username, @PathVariable String password,
            @PathVariable String email, HttpServletRequest request) {
        User newUser = new User();
        newUser.setUserName(username);
        newUser.setPassword(password);
        newUser.setEmail(email);
        accountRepository.save(newUser);
        URI location = ServletUriComponentsBuilder.fromRequestUri(request)
                .path("/{id}")
                .buildAndExpand(newUser.getId())
                .toUri();
        return ResponseEntity.created(location).body(newUser);
    }

    @GetMapping(value = "/addgroup/{groupName}/{groupType}")
    public @ResponseBody String addGroup(@PathVariable String groupName, @PathVariable String groupType) {
        RunnerGroup newGroup = new RunnerGroup();
        newGroup.setGroupName(groupName);
        newGroup.setGroupType(groupType);
        groupRepository.save(newGroup);

        return groupName + " of type " + groupType + " has been added to the database.";
    }

}