package com.pvt.groupOne.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.pvt.groupOne.model.Run;
import java.util.List;

public interface RunRepository extends JpaRepository<Run, Integer> {
    List<Run> findAllByOrderByTimestampAsc();
    List<Run> findAllByOrderByAvgSpeedKmhAsc();
    List<Run> findAllByOrderByDistanceInMetersAsc();
    List<Run> findAllByOrderByTimeInMillisAsc();
    List<Run> findAllByOrderByCaloriesBurnedAsc();

    Run save(Run run);
    void delete(Run run);

    @Query("SELECT SUM(timeInMillis) FROM running_table")
    Long getTotalTimeInMillis();

    @Query("SELECT SUM(caloriesBurned) FROM running_table")
    Long getTotalCaloriesBurned();

    @Query("SELECT SUM(distanceInMeters) FROM running_table")
    int getTotalDistanceInMeters();

    @Query("SELECT AVG(timeInMillis) FROM running_table")
    float getAvgTimeInMillis();
}