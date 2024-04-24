package com.pvt.groupOne.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pvt.groupOne.Service.EmailService;

@RestController
public class EmailController {

    @Autowired
    private EmailService emailService;

    @PostMapping("/sendEmail/{email}")
    public String sendEmail(@PathVariable String reciever, 
                            @RequestParam String subject, 
                            @RequestParam String text) {
        emailService.sendSimpleMessage(reciever, subject, text);
        return "Email Sent!";
    }
}

