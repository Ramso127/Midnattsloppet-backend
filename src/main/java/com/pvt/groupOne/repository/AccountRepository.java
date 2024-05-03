package com.pvt.groupOne.repository;

import com.pvt.groupOne.model.User;
import com.pvt.groupOne.model.UserInfo;

import org.springframework.data.repository.CrudRepository;

public interface AccountRepository extends CrudRepository<User, String> {
    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    User findByEmail(String email);

    User findByUsername(String username);

    UserInfo findUserDetailsByUsername(String username);
}
