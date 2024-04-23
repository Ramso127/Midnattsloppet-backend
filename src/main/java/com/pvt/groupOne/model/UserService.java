package com.pvt.groupOne.model;

import com.pvt.groupOne.repository.PasswordTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserService {

    @Autowired
    private PasswordTokenRepository passwordTokenRepository ;

    public PasswordResetToken createPasswordResetToken(User user){
        String token = generateToken();
        PasswordResetToken passwordResetToken = new PasswordResetToken(token, user);
        passwordTokenRepository.save(passwordResetToken);
        return passwordResetToken;
    }

    private String generateToken(){
        return java.util.UUID.randomUUID().toString();
    }
}
