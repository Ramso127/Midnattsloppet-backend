package com.pvt.groupOne.repository;

import org.springframework.data.repository.CrudRepository;

import com.pvt.groupOne.model.Image;

public interface ImageRepository extends CrudRepository<Image, String> {
    Image findByuserName(String username);
}
