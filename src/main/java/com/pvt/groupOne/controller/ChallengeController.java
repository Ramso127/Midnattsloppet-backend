package com.pvt.groupOne.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.pvt.groupOne.model.Challenge;
import com.pvt.groupOne.repository.ChallengeRepository;

@Controller
@RequestMapping(path = "/challenges")
@CrossOrigin
public class ChallengeController {

    @Autowired
    private ChallengeRepository challengeRepository;

    private final String kilometers = "kilometers";
    private final String numberOfRuns = "runs";
    private final String minutesPerKilometer = "min / km";

    @PutMapping(value = "/increment-challenges")
    public @ResponseBody String incrementChallenges() {

        int nextID;

        Challenge currentActiveChallenge = challengeRepository.findByisActive(true);
        currentActiveChallenge.setActive(false);
        challengeRepository.save(currentActiveChallenge);
        int currentID = currentActiveChallenge.getId();

        // If the last challenge was the final one, loop back to the first challenge.
        if (currentID == 6) {
            nextID = 0;
            currentID = 0;
        }
        nextID = currentID + 1;
        Challenge nextActiveChallenge = challengeRepository.findByid(nextID);
        nextActiveChallenge.setActive(true);
        challengeRepository.save(nextActiveChallenge);
        return "Current active challenge: #" + nextActiveChallenge.getId();
    }

    @PutMapping(value = "/set-first-challenge-as-active")
    public @ResponseBody String setFirstChallengeAsActive() {

        Challenge firstChallenge = challengeRepository.findByid(1);
        firstChallenge.setActive(true);
        challengeRepository.save(firstChallenge);

        return "First challenge set as active.";

    }

    @PostMapping(value = "/add-challenges")
    public @ResponseBody String addChallenges() {

        Challenge firstChallenge = new Challenge("Furthest distance",
                "Run the furthest distance. Minimum pace is 8 minutes per kilometer." , kilometers);
        Challenge secondChallenge = new Challenge("Most runs", "Run the most runs. Minimum 2 kilometers per run.", numberOfRuns);
        Challenge thirdChallenge = new Challenge("Furthest run per member",
                "The longest run of every team member will be added to the total. Minimum pace is 8 minutes per kilometer.", kilometers);
        Challenge fourthChallenge = new Challenge("Highest average pace",
                "Have the lowest average minutes per kilometer. Minimum distance per run is 2 kilometers.", minutesPerKilometer);
        Challenge fifthChallenge = new Challenge("Most long runs",
                "Have the highest number of runs with a minimum distance of 5 kilometers.", numberOfRuns);
        Challenge sixthChallenge = new Challenge("Total number of faster runs",
                "Have the highest number of runs with a minimum pace of 6 minutes per kilometer.", numberOfRuns);

        firstChallenge.setActive(true);

        challengeRepository.save(firstChallenge);
        challengeRepository.save(secondChallenge);
        challengeRepository.save(thirdChallenge);
        challengeRepository.save(fourthChallenge);
        challengeRepository.save(fifthChallenge);
        challengeRepository.save(sixthChallenge);

        return "Challenges successfully added.";
    }

    @PutMapping(value = "/set-all-as-inactive")
    public @ResponseBody String setAllAsInactive() {

        for (int i = 1; i <= 6; i++) {
            Challenge currentChallenge = challengeRepository.findByid(i);
            currentChallenge.setActive(false);
            challengeRepository.save(currentChallenge);
        }

        return "All challenges set as inactive.";
    }

    @GetMapping(value = "/get-current-challenge")
    public @ResponseBody Map<String, List<Map<String, String>>> getCurrentChallenge() {

        Challenge currentChallenge = challengeRepository.findByisActive(true);
        List<Map<String, String>> mapList = new ArrayList<>();
        Map<String, String> challengeMap = new HashMap<>();
        challengeMap.put("title", currentChallenge.getTitle());
        challengeMap.put("description", currentChallenge.getDescription());
        challengeMap.put("measuredStat", currentChallenge.getMeasuredStat());
        mapList.add(challengeMap);

        Map<String, List<Map<String, String>>> wrapperMap = new HashMap<>();
        wrapperMap.put("data", mapList);

        return wrapperMap;
    }

}
