package com.pvt.groupOne.repository;

import org.springframework.data.repository.CrudRepository;

import com.pvt.groupOne.model.Challenge;

public interface ChallengeRepository extends CrudRepository<Challenge, Integer> {
    Challenge findChallangeById(Integer id);

    Challenge findByTitle(String title);

}
