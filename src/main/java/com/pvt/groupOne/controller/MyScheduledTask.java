package com.pvt.groupOne.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.pvt.groupOne.model.User;
import com.pvt.groupOne.repository.AccountRepository;

import java.time.DayOfWeek;
import java.time.LocalDateTime;

@Component
public class MyScheduledTask {

    @Autowired
    private AccountRepository accountRepository;

    @Scheduled(cron = "0 20 16 * * TUE", zone = "Europe/Stockholm") // Run every Sunday at midnight
    public void runTask() {
        // Your method logic goes here
        User testUser = new User("testusername", "testpassword", "testemail@gmail.com", "testcompany");
        accountRepository.save(testUser);
        System.out.println("Task executed on " + LocalDateTime.now());
    }
}