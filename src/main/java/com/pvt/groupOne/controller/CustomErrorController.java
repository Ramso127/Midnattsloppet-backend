package com.pvt.groupOne.controller;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@CrossOrigin
public class CustomErrorController implements ErrorController {

    @RequestMapping("/error")
    public String handleError() {
        return "forward:/error.html";
    }
    public String getErrorPath() {
        return "/error";
    }
}