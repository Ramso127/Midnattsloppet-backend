package com.pvt.groupOne.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import com.pvt.groupOne.Service.EmailService;

@Controller
@RequestMapping(path = "/email")
@CrossOrigin
public class EmailController {

    @Autowired
    private EmailService emailService;

    @PostMapping("/sendEmail/{reciever}")
    public String sendEmail(@PathVariable String reciever, 
                            @RequestParam String subject, 
                            @RequestParam String text) {
        emailService.sendSimpleMessage(reciever, subject, text);
        return "Email Sent!";
    }
}

