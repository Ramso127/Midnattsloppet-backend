package com.pvt.groupOne.repository;

import com.pvt.groupOne.model.User;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface AccountRepository extends CrudRepository<User, String> {
    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    List<User> findByRunnerGroup_GroupId(int groupId);
    User findByEmail(String email);

    User findByUsername(String username);

}
