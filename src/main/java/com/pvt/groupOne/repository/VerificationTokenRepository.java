package com.pvt.groupOne.repository;

import com.pvt.groupOne.model.User;
import com.pvt.groupOne.model.VerificationToken;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface VerificationTokenRepository extends CrudRepository<VerificationToken, Long> {

    VerificationToken findByToken(String token);

    @Query("SELECT v.user FROM VerificationToken v WHERE v.token = :token")
    User findUserByToken(@Param("token") String token);
}
