package com.pvt.groupOne.controller;

import com.pvt.groupOne.Service.TokenService;
import com.pvt.groupOne.repository.VerificationTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping(path = "/route")
@CrossOrigin
public class WebRouterController {

    @Autowired
    TokenService tokenService;

    @Autowired
    VerificationTokenRepository verificationTokenRepository;

    @GetMapping("/resetPassword")
    public String servePasswordReset(@RequestParam("token") String token) {
            String result = tokenService.validatePasswordResetToken(token);
            if (result.equals("202")) {
                return "forward:/resetPassword.html";
            }
            else if(result.equals("invalid")){
                return "forward:/invalidPasswordResetToken.html";
            }
            else{
                return "forward:/passwordError.html";
            }
    }

    @GetMapping("/emailVerification")
    public String serveVerifyEmail(@RequestParam("token") String token) {
        if(tokenService.isVerifiedAlready(verificationTokenRepository.findUserByToken(token))){
            return "forward:/emailAlreadyVerified.html";
        }
        else {
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
}
