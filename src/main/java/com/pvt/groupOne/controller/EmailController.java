package com.pvt.groupOne.controller;

import com.pvt.groupOne.Service.TokenService;
import com.pvt.groupOne.model.EmailRequest;
import com.pvt.groupOne.model.PasswordResetToken;
import com.pvt.groupOne.model.User;
import com.pvt.groupOne.model.VerificationToken;
import com.pvt.groupOne.repository.AccountRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import com.pvt.groupOne.Service.EmailService;

@Controller
@RequestMapping(path = "/email")
@CrossOrigin
public class EmailController {

    @Autowired
    private EmailService emailService;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TokenService tokenService;

    @PostMapping("/send-email/{reciever}")
    public String sendEmail(@PathVariable String reciever,
            @RequestBody EmailRequest emailRequest) {
        emailService.sendSimpleMessage(reciever, emailRequest.getSubject(), emailRequest.getText());
        return "Email Sent!";
    }

    @GetMapping(value = "/reset-password/{email}")
    public @ResponseBody String resetPassword(HttpServletRequest request, @PathVariable String email) {
        if (!accountRepository.existsByEmail(email)) {
            return "No such user exists";

        } else {
            User user = accountRepository.findByEmail(email);

            PasswordResetToken token = tokenService.createPasswordResetToken(user);

            String url = "https://" + request.getServerName() + "/" + request.getContextPath()
                    + "route/reset-password?token=" + token.getToken();
            EmailRequest emailRequest = new EmailRequest();
            emailRequest.setSubject("Reset Password");
            emailRequest.setText("Hello " + user.getUsername() + ", \r\n" +
                    "Please click the following link to reset your password: \r\n" + url);
            sendEmail(user.getEmail(), emailRequest);

            return "Email has been sent";
        }

    }

    @GetMapping(value = "/verify-mail/{email}")
    public @ResponseBody String verificationCode(HttpServletRequest request, @PathVariable String email) {
        if (!accountRepository.existsByEmail(email)) {
            return "No such user exists";

        } else {
            User user = accountRepository.findByEmail(email);

            VerificationToken token = tokenService.createVerificationCode(user);

            String url = "https://" + request.getServerName() + "/" + request.getContextPath()
                    + "route/email-verification?token=" + token.getToken();
            EmailRequest emailRequest = new EmailRequest();
            emailRequest.setSubject("Mail Verification");
            emailRequest.setText("Hello " + user.getUsername() + ", \r\n" +
                    "Please click the following link to verify your email: \r\n" + url);
            sendEmail(user.getEmail(), emailRequest);

            return "Email has been sent";
        }

    }

    @GetMapping(value = "/check-verification/{username}")
    public ResponseEntity<String> checkVerification(@PathVariable String username) {

        User currentUser = accountRepository.findByUsername(username);

        if (currentUser.isVerified()) {
            return ResponseEntity.ok("{\"message\": \"User is verified!\"}");
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"message\": \"User is not verified!\"}");
    }

}
