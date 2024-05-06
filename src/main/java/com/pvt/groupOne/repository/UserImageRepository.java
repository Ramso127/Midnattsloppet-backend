package com.pvt.groupOne.repository;

import com.pvt.groupOne.model.UserImage;
import jakarta.persistence.Table;
import org.springframework.data.repository.CrudRepository;

@Table
public interface UserImageRepository extends CrudRepository<UserImage, String> {
    UserImage findByUserName(String username);
}
