package com.pvt.groupOne.Service;

import com.pvt.groupOne.model.PasswordResetToken;
import com.pvt.groupOne.model.User;
import com.pvt.groupOne.repository.PasswordTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;
@Service
public class PasswordResetService {

    @Autowired
    PasswordTokenRepository passwordTokenRepository;

    public PasswordResetToken createPasswordResetToken(User user) {
        String token = generateToken();
        PasswordResetToken passwordResetToken = new PasswordResetToken(token, user);
        passwordTokenRepository.save(passwordResetToken);
        return passwordResetToken;
    }

    private String generateToken() {
        return java.util.UUID.randomUUID().toString();
    }

    public String validatePasswordResetToken(String token) {
        PasswordResetToken passReset = passwordTokenRepository.findByToken(token);
        Calendar time = Calendar.getInstance();

        if (token == null) {
            return "The token is invalid. Please request a new one.";
        } else if (passReset.getExpiryDate().before(time.getTime())) {
            return "The token is expired. Please request a new one.";
        }
        return "202";
    }


}
