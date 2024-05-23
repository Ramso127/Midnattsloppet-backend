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


    @Query("SELECT v FROM PasswordResetToken v WHERE v.user = :user AND v.depleted = false ORDER BY v.expiryDate DESC limit 1")
    PasswordResetToken findFirstByUserOrderByExpiryDateDesc(@Param("user") User user);

    @Query("SELECT v.depleted FROM PasswordResetToken v WHERE v.token = :token")
    boolean getIfDepleted(@Param("token") String token);

    @Query("SELECT v.user FROM PasswordResetToken v WHERE v.token = :token")
    User findUserByToken(@Param("token") String token);
}
