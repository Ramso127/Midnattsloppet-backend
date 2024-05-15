package com.pvt.groupOne.Service;

import com.pvt.groupOne.model.PasswordResetToken;
import com.pvt.groupOne.model.User;
import com.pvt.groupOne.model.VerificationToken;
import com.pvt.groupOne.repository.AccountRepository;
import com.pvt.groupOne.repository.PasswordTokenRepository;
import com.pvt.groupOne.repository.VerificationTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.UUID;

@Service
public class TokenService {

    @Autowired
    PasswordTokenRepository passwordTokenRepository;

    @Autowired
    VerificationTokenRepository verificationTokenRepository;

    @Autowired
    AccountRepository accountRepository;

    public PasswordResetToken createPasswordResetToken(User user) {
        String token = generateToken();
        PasswordResetToken passwordResetToken = new PasswordResetToken(token, user);
        passwordTokenRepository.save(passwordResetToken);
        return passwordResetToken;
    }

    public VerificationToken createVerificationCode(User user) {
        String token = generateToken();
        VerificationToken verifyToken = new VerificationToken(token, user);
        verificationTokenRepository.save(verifyToken);
        return verifyToken;
    }

    private String generateToken() {
        return UUID.randomUUID().toString();
    }


    public String validateVerificationMail(String token) {
        VerificationToken passReset = verificationTokenRepository.findByToken(token);
        Calendar time = Calendar.getInstance();

        if (passReset == null || token.length() != 36) {
            return "invalid";
        } else if (passReset.getExpiryDate().before(time.getTime())) {
            return "expired";
        }
        return "202";
    }

    public String validatePasswordResetToken(String token) {
        PasswordResetToken passReset = passwordTokenRepository.findByToken(token);
        Calendar time = Calendar.getInstance();

        if (passReset == null || token.length() != 36) {
            return "invalid";
        } else if (passReset.getExpiryDate().before(time.getTime())) {
            return "expired";
        }
        return "202";
    }

    public boolean verifyUser(User verificationUser) {
        User user = accountRepository.findByUsername(verificationUser.getUsername());
        user.setVerified(true);

        User savedUser = accountRepository.save(user);

        return savedUser != null && savedUser.isVerified();
    }

    public boolean isVerifiedAlready(User verificationUser) {
    User user = accountRepository.findByUsername(verificationUser.getUsername());
    return user != null && user.isVerified();
}


}
