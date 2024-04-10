package com.pvt.groupOne.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(path = "/test")
@CrossOrigin
public class WilleTest {

    @GetMapping(value = "/WilhelmHello")
    public @ResponseBody String testMethod() {
        return "Hello this is Wilhelm";
    }

}
