package com.pvt.groupOne.repository;

import com.pvt.groupOne.model.PasswordResetToken;
import com.pvt.groupOne.model.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PasswordTokenRepository extends CrudRepository<PasswordResetToken, Long> {

    PasswordResetToken findByToken(String token);

    @Query("SELECT v.user FROM PasswordResetToken v WHERE v.token = :token")
    User findUserByToken(@Param("token") String token);
}
