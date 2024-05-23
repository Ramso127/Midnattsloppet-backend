package com.pvt.groupOne.Service;

import com.pvt.groupOne.model.PasswordResetToken;
import com.pvt.groupOne.model.User;
import com.pvt.groupOne.model.VerificationToken;
import com.pvt.groupOne.repository.AccountRepository;
import com.pvt.groupOne.repository.PasswordTokenRepository;
import com.pvt.groupOne.repository.VerificationTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Calendar;
import java.util.UUID;

@Service
public class TokenService {

    @Autowired
    private PasswordTokenRepository passwordTokenRepository;

    @Autowired
    private VerificationTokenRepository verificationTokenRepository;

    @Autowired
    private AccountRepository accountRepository;

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
        }
        else if (passwordTokenRepository.getIfDepleted(passReset.getToken())) {
            return "101";
        }
        else if (passReset.getExpiryDate().before(time.getTime())) {
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

        if (user != null) {
            return user.isVerified();
        }
        return false;
    }

    public String depletePasswordResetToken(String token){
        PasswordResetToken passReset = passwordTokenRepository.findByToken(token);
        passReset.setDepleted(true);
        PasswordResetToken passwordResetToken = passwordTokenRepository.save(passReset);
        return passwordResetToken != null? "202" : "404";
    }

    public String depleteRecentResetToken(String username) {
        User user = accountRepository.findByUsername(username);
        if(user == null) {
            return "404";
        }
        PasswordResetToken passReset = passwordTokenRepository.findFirstByUserOrderByExpiryDateDesc(user);
        passReset.setDepleted(true);
        PasswordResetToken passwordResetToken = passwordTokenRepository.save(passReset);
        return passwordResetToken != null? "202" : "404";
    }

}
