package com.pvt.groupOne.repository;

import org.springframework.data.repository.CrudRepository;

import com.pvt.groupOne.model.UserInfo;

public interface AccountInfoRepository extends CrudRepository<UserInfo, Integer> {
    UserInfo findByUser_username(String username);
}
