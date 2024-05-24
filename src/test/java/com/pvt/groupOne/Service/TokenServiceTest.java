package com.pvt.groupOne.Service;

import com.pvt.groupOne.model.PasswordResetToken;
import com.pvt.groupOne.model.User;
import com.pvt.groupOne.repository.PasswordTokenRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class TokenServiceTest {

    @Mock
    private PasswordTokenRepository passwordTokenRepository;

    @InjectMocks
    private TokenService tokenService;

    @Test
    public void testCreatePasswordResetToken() {
        User user = new User();
        when(passwordTokenRepository.save(any(PasswordResetToken.class))).thenAnswer(i -> i.getArguments()[0]);
        PasswordResetToken token = tokenService.createPasswordResetToken(user);
        assertNotNull(token);
        verify(passwordTokenRepository).save(token);
    }
    // TODO Noa ta bort eller fixa klart?
    @Test
    public void testValidatePasswordResetToken() {
//        String tokenString = java.util.UUID.randomUUID().toString();
//        User user = new User();
//        Calendar cal = Calendar.getInstance();
//        cal.add(Calendar.HOUR, 1);
//        PasswordResetToken token = new PasswordResetToken(tokenString, user);
//        token.setExpiryDate(cal.getTime());
//
//        when(passwordTokenRepository.findByToken(tokenString)).thenReturn(token);
//        String status = tokenService.validatePasswordResetToken(tokenString);
//        assertEquals("202", status);
//
//
//        cal.add(Calendar.HOUR, -3);
//        token.setExpiryDate(cal.getTime());
//        status = tokenService.validatePasswordResetToken(tokenString);
//        assertTrue(status.contains("expired"));
//
//        PasswordResetToken invalidToken = new PasswordResetToken("test", user);
//        cal.add(Calendar.HOUR, 4);
//        invalidToken.setExpiryDate(cal.getTime());
//        when(passwordTokenRepository.findByToken(invalidToken.getToken())).thenReturn(invalidToken);
//        status = tokenService.validatePasswordResetToken(invalidToken.getToken());
//        assertTrue(status.contains("invalid"));
    }
}