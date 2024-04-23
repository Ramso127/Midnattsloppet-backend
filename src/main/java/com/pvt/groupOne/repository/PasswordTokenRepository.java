package com.pvt.groupOne.repository;

import com.pvt.groupOne.model.PasswordResetToken;
import org.springframework.data.repository.CrudRepository;

public interface PasswordTokenRepository extends CrudRepository<PasswordResetToken, Long> {

    PasswordResetToken findByToken(String token) ;
}
