package com.pvt.groupOne.controller;

import com.pvt.groupOne.Service.PasswordResetService;
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
    PasswordResetService passwordResetService;

    @GetMapping("/resetPassword")
    public String servePasswordReset(@RequestParam("token") String token) {
            String result = passwordResetService.validatePasswordResetToken(token);
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
}
