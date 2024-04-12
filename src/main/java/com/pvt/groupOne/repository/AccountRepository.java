package com.pvt.groupOne.repository;

import com.pvt.groupOne.model.User;
import org.springframework.data.repository.CrudRepository;

public interface AccountRepository extends CrudRepository<User, String> {
    boolean existsByUsername(String username);

    User findByUsername(String username);
}
