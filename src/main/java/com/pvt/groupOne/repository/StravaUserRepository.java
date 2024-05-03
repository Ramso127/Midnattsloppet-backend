package com.pvt.groupOne.repository;

import org.springframework.data.repository.CrudRepository;

import com.pvt.groupOne.model.StravaUser;

public interface StravaUserRepository extends CrudRepository<StravaUser, Integer> {
    StravaUser findById(int id);

}