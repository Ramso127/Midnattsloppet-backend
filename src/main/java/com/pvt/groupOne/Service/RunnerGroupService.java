package com.pvt.groupOne.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pvt.groupOne.model.RunnerGroup;
import com.pvt.groupOne.model.User;
import com.pvt.groupOne.repository.RunnerGroupRepository;
import java.util.UUID;

@Service
public class RunnerGroupService {

    private final RunnerGroupRepository runnerGroupRepository;

    @Autowired
    public RunnerGroupService(RunnerGroupRepository runnerGroupRepository) {
        this.runnerGroupRepository = runnerGroupRepository;
    }

    public RunnerGroup createRunnerGroup(String companyName, String teamName, byte[] image, User user) {
        RunnerGroup runnerGroup = new RunnerGroup();
        runnerGroup.setCompanyName(companyName);
        runnerGroup.setTeamName(teamName);
        runnerGroup.setGroupPicture(image);
        runnerGroup.addUser(user);
        runnerGroup.setInviteCode(generateInviteCode());
        runnerGroupRepository.save(runnerGroup);
        return runnerGroupRepository.save(runnerGroup);
    }

    private String generateInviteCode() {
        String inviteCode;
        boolean isUnique = false;
        do {
            inviteCode = UUID.randomUUID().toString().substring(0, 8); // You can adjust the length as needed
            // Check if the invite code is unique
            isUnique = !runnerGroupRepository.existsByInviteCode(inviteCode);
        } while (!isUnique);
        return inviteCode;
    }
}
