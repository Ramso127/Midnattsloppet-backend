package com.pvt.groupOne.Service;

import com.pvt.groupOne.model.PasswordResetToken;
import com.pvt.groupOne.repository.PasswordTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pvt.groupOne.model.User;
import com.pvt.groupOne.repository.AccountRepository;

import java.util.Calendar;

@Service
public class UserService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private PasswordTokenRepository passwordTokenRepository;

    public boolean authenticateUser(String username, String password) {
        // Retrieve user information based on the username
        User user = accountRepository.findByUsername(username);
        if (user == null) {
            // User not found
            return false;
        }

        // Verify password
        if (password.equals(user.getPassword())) {
            // Passwords match, login successful
            return true;
        }

        // Passwords do not match, login failed
        return false;
    }


}
