package com.pvt.groupOne.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(path = "/route")
@CrossOrigin
public class WebRouterController {

    @GetMapping("/passwordReset")
    public String servePasswordReset() {
        return "forward:/static/passwordReset.html";
    }
}
