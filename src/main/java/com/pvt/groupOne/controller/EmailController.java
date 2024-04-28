package com.pvt.groupOne.controller;

import com.pvt.groupOne.Service.UserService;
import com.pvt.groupOne.model.EmailRequest;
import com.pvt.groupOne.model.PasswordResetToken;
import com.pvt.groupOne.model.User;
import com.pvt.groupOne.repository.AccountRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import com.pvt.groupOne.Service.EmailService;

import java.util.Locale;

@Controller
@RequestMapping(path = "/email")
@CrossOrigin
public class EmailController {

    @Autowired
    private EmailService emailService;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private UserService userService;


    @PostMapping("/sendEmail/{reciever}")
    public String sendEmail(@PathVariable String reciever,
                            @RequestBody EmailRequest emailRequest) {
        emailService.sendSimpleMessage(reciever, emailRequest.getSubject(), emailRequest.getText());
        return "Email Sent!";
    }

    public String sendResetTokenMail(String contextPath, Locale locale, PasswordResetToken token, User user) {
        String url = contextPath + "/resetPassword?token=" + token;
        EmailRequest emailRequest = new EmailRequest();
        emailRequest.setSubject("Reset Password");
        emailRequest.setText("Hello " + user.getUserName() + ", \r\n" +
                "Please click the following link to reset your password: \r\n" + url);
        return sendEmail(user.getEmail(), emailRequest);
    }

    @GetMapping(value = "/resetPassword/{email}")
    public @ResponseBody String resetPassword(HttpServletRequest request, @PathVariable String email) {
        if (!accountRepository.existsByEmail(email)) {
            return "No such user exists";

        } else {
            User user = accountRepository.findByEmail(email);

            PasswordResetToken token = userService.createPasswordResetToken(user);

            String url = request.getContextPath() + "/resetPassword?token=" + token.toString();
            EmailRequest emailRequest = new EmailRequest();
            emailRequest.setSubject("Reset Password");
            emailRequest.setText("Hello " + user.getUserName() + ", \r\n" +
                    "Please click the following link to reset your password: \r\n" + url);
            sendEmail(user.getEmail(), emailRequest);

            return "Email has been sent";
        }

        // return "Password has been reset";
    }

}

