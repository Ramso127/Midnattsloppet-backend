package com.pvt.groupOne.repository;

import org.springframework.data.repository.CrudRepository;

import com.pvt.groupOne.model.Challenge;

public interface ChallengeRepository extends CrudRepository<Challenge, String> {
    Challenge findByTitle(String username);

}
