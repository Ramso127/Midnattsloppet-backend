package com.pvt.groupOne.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import com.pvt.groupOne.repository.Group;
import com.pvt.groupOne.repository.GroupRepository;
import com.pvt.groupOne.repository.User;
import com.pvt.groupOne.repository.UserRepository;

@Controller
@RequestMapping(path = "/test")
@CrossOrigin
public class DiddeTest {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private GroupRepository groupRepository;

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
        Group newGroup = new Group();
        newGroup.setGroupName(groupName);
        newGroup.setGroupType(groupType);
        groupRepository.save(newGroup);

        return groupName + " of type " + groupType + " has been added to the database.";
    }

}