package com.pvt.groupOne.Service;

import com.pvt.groupOne.model.PasswordEncryption;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pvt.groupOne.model.User;
import com.pvt.groupOne.repository.AccountRepository;

@Service
public class UserService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private PasswordEncryption passwordEncryption;

    public boolean authenticateUser(String username, String password) {
        User user = accountRepository.findByUsername(username);
        if (user == null) {
            return false;
        }
        return passwordEncryption.passwordEncoder().matches(password, user.getPassword());
    }

}
