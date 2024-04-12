package com.pvt.groupOne.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pvt.groupOne.model.User;
import com.pvt.groupOne.model.UserInfo;
import com.pvt.groupOne.repository.AccountRepository;
import org.springframework.security.crypto.password.PasswordEncoder;

@Service
public class UserService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public boolean authenticateUser(String username, String password) {
        // Retrieve user information based on the username
        User user = accountRepository.findByUsername(username);
        if (user == null) {
            // User not found
            return false;
        }

        // Retrieve userinfo associated with the user

        // Verify password
        if (passwordEncoder.matches(password, user.getPassword())) {
            // Passwords match, login successful
            return true;
        }

        // Passwords do not match, login failed
        return false;
    }
}
