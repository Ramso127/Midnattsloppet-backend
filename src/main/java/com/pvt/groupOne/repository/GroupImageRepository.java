package com.pvt.groupOne.repository;

import com.pvt.groupOne.model.GroupImage;
import jakarta.persistence.Table;
import org.springframework.data.repository.CrudRepository;

@Table
public interface GroupImageRepository extends CrudRepository<GroupImage, String> {
    GroupImage findByGroupName(String groupName);
}
