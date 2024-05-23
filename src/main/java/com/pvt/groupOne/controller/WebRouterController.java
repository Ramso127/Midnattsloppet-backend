package com.pvt.groupOne.controller;

import com.pvt.groupOne.Service.TokenService;
import com.pvt.groupOne.model.PasswordEncryption;
import com.pvt.groupOne.model.PasswordResetToken;
import com.pvt.groupOne.model.User;
import com.pvt.groupOne.repository.AccountRepository;
import com.pvt.groupOne.repository.PasswordTokenRepository;
import com.pvt.groupOne.repository.VerificationTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
@RequestMapping(path = "/route")
@CrossOrigin
public class WebRouterController {

    @Autowired
    TokenService tokenService;

    @Autowired
    VerificationTokenRepository verificationTokenRepository;

    @Autowired
    PasswordTokenRepository passwordTokenRepository;

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    PasswordEncryption passwordEncryption;

    @GetMapping("/resetPassword")
    public String servePasswordReset(@RequestParam("token") String token) {
            String result = tokenService.validatePasswordResetToken(token);
            if (result.equals("202")) {
                return "forward:/resetPassword.html";
            }
            else if(result.equals("101")){
                return "forward:/depletedPasswordResetToken.html";
            }
            else if(result.equals("invalid")){
                return "forward:/invalidPasswordResetToken.html";
            }
            else{
                return "forward:/passwordError.html";
            }
    }

    @PostMapping("/save-password")
    @ResponseBody
    public ResponseEntity<?> saveNewPassword(@RequestBody Map<String, String> payload) {
        String newPassword = payload.get("newPassword");
        String token = payload.get("token");

        String result = tokenService.validatePasswordResetToken(token);
        User user = passwordTokenRepository.findUserByToken(token);

        if(result == "202" ) {
            if (user == null) {
                return ResponseEntity.status(404).body("User not found");
            } else {
                user.setPassword(passwordEncryption.passwordEncoder().encode(newPassword));
                PasswordResetToken passwordResetToken = passwordTokenRepository.findByToken(token);
                tokenService.depletePasswordResetToken(token);
                passwordResetToken.setDepleted(true);
                accountRepository.save(user);
                return ResponseEntity.ok().build();
            }
        }
        else if(result.equals("101")){
            return ResponseEntity.status(404).body("Password has already been reset");
        }
        else{
            return ResponseEntity.status(404).body("Invalid Token");
        }
    }

    @GetMapping("/emailVerification")
    public String serveVerifyEmail(@RequestParam("token") String token) {
        User user = verificationTokenRepository.findUserByToken(token);
        if(user != null) {
            if (user.isVerified()) {
                return "forward:/emailAlreadyVerified.html";
            }
        }
        String result = tokenService.validateVerificationMail(token);
        if (result.equals("202") && tokenService.verifyUser(verificationTokenRepository.findUserByToken(token))) {
            return "forward:/emailSuccess.html";
        } else if (result.equals("invalid")) {
            return "forward:/invalidEmailVerificationToken.html";
        } else {
            return "forward:/emailError.html";
            }
        }
}
