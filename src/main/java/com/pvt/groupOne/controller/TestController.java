package com.pvt.groupOne.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.pvt.groupOne.model.User;
import com.pvt.groupOne.repository.AccountRepository;

@Controller
@RequestMapping(path = "/testing")
@CrossOrigin
public class TestController {

    @Autowired
    private AccountRepository accountRepository;

    @GetMapping(value = "/force-verify/{username}")
    public ResponseEntity<String> forceVerify(@PathVariable String username) {

        if (accountRepository.findByUsername(username) == null){
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"message\": \"User not found!\"}");
        }

        User currentUser = accountRepository.findByUsername(username);
        currentUser.setVerified(true);
        accountRepository.save(currentUser);
        return ResponseEntity.ok("{\"message\": \"User is verified!\"}");

    }
    
}
