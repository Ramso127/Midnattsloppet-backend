package com.pvt.groupOne.repository;

import com.pvt.groupOne.model.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Integer> {
    
}
