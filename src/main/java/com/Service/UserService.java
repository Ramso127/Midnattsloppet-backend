package com.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pvt.groupOne.model.User;
import com.pvt.groupOne.model.UserInfo;
import com.pvt.groupOne.repository.AccountRepository;

@Service
public class UserService {

    @Autowired
    private AccountRepository accountRepository;
    // ADD PASSWORD ENCODER HERE

    public boolean authenticateUser(String username, String password) {
        // Retrieve user information based on the username
        User user = accountRepository.findByUsername(username);
        if (user == null) {
            // User not found
            return false;
        }

        // Retrieve userinfo associated with the user
        UserInfo userInfo = user.getUserInfo();

        // Verify password
        if (userInfo != null && password.equals(user.getPassword())) {
            // Passwords match, login successful
            return true;
        }

        // Passwords do not match, login failed
        return false;
    }
}
