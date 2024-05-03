package com.pvt.groupOne.repository;

import com.pvt.groupOne.model.RunnerGroup;
import org.springframework.data.repository.CrudRepository;

public interface RunnerGroupRepository extends CrudRepository<RunnerGroup, Integer> {
    boolean existsByGroupName(String groupName);

    boolean existsByInviteCode(String inviteCode);

    RunnerGroup findGroupByInviteCode(String inviteCode);
}
