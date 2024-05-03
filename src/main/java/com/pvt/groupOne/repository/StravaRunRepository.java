package com.pvt.groupOne.repository;

import org.springframework.data.repository.CrudRepository;

import com.pvt.groupOne.model.StravaRun;
import com.pvt.groupOne.model.StravaUser;

public interface StravaRunRepository extends CrudRepository<StravaRun, String> {
    // TODO DIDDE: This is a dumb test method, remove later
    StravaUser findByDate(String date);

}