package com.pvt.groupOne.Service;

import com.pvt.groupOne.model.Run;
import com.pvt.groupOne.repository.RunRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

@Service
public class RunService {

    @Autowired
    private RunRepository runRepository;

    public void saveRun(Run run) {
        runRepository.save(run);
    }

    // Get top 3 runs for a specific user
    public List<Run> getTopThreeRunsByUser(String username) {
        return runRepository.findTop3RunsByDistance(username);
    }

    // Get top 3 users based on total distance run during the current week
    public List<Object[]> getTopThreeUsersByDistanceThisWeek() {
        LocalDate today = LocalDate.now();
        LocalDate startOfWeek = today.with(DayOfWeek.MONDAY);  // Assumes Monday is the start of the week
        LocalDate endOfWeek = today.with(DayOfWeek.SUNDAY);

        return runRepository.findTop3UsersByWeeklyDistance(startOfWeek, endOfWeek);
    }
}
