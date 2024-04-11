package com.pvt.groupOne.controller;

import com.pvt.groupOne.repository.Account;
import com.pvt.groupOne.repository.GroupRunners;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
        Account newAccount = new Account();
        newAccount.setUserName(username);
        newAccount.setPassword(password);
        newAccount.setEmail(email);
        accountRepository.save(newAccount);

        return "Account " + username + " with password " + password + " has been added to the database.";
    }

    @GetMapping(value = "/addgroup/{groupName}/{groupType}")
    public @ResponseBody String addGroup(@PathVariable String groupName, @PathVariable String groupType) {
        GroupRunners newGroup = new GroupRunners();
        newGroup.setGroupName(groupName);
        newGroup.setGroupType(groupType);
        groupRepository.save(newGroup);

        return groupName + " of type " + groupType + " has been added to the database.";
    }

}