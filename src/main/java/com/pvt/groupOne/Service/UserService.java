package com.pvt.groupOne.Service;

import com.pvt.groupOne.model.PasswordResetToken;
import com.pvt.groupOne.repository.PasswordTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pvt.groupOne.model.User;
import com.pvt.groupOne.model.UserInfo;
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

        // Retrieve userinfo associated with the user
        UserInfo userInfo = user.getUserInfo();

        // Verify password
        if (password.equals(user.getPassword())) {
            // Passwords match, login successful
            return true;
        }

        // Passwords do not match, login failed
        return false;
    }

    public PasswordResetToken createPasswordResetToken(User user){
        String token = generateToken();
        PasswordResetToken passwordResetToken = new PasswordResetToken(token, user);
        passwordTokenRepository.save(passwordResetToken);
        return passwordResetToken;
    }

    private String generateToken(){
        return java.util.UUID.randomUUID().toString();
    }

    public String validatePasswordResetToken(String token) {
        PasswordResetToken passReset = passwordTokenRepository.findByToken(token);

        if(!isTokenFound(passReset)) {
            return "The token is invalid. Please request a new one.";
        }
        else if(isTokenExpired(passReset)) {
            return "The token is expired. Please request a new one.";
        }
        return null;
    }

    private boolean isTokenFound(PasswordResetToken token) {
        return token != null;
    }

    private boolean isTokenExpired(PasswordResetToken token) {
        Calendar time = Calendar.getInstance();
        return token.getExpiryDate().before(time.getTime());
    }


}
