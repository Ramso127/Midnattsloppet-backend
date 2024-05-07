package com.pvt.groupOne.repository;

import com.pvt.groupOne.model.Run;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.time.LocalDate;
import java.util.List;

public interface RunRepository extends JpaRepository<Run, Long> {
    // Find top 3 runs for a user ordered by distance
    @Query("SELECT r FROM Run r WHERE r.user.username = :username ORDER BY r.totalDistance DESC")
    List<Run> findTop3RunsByDistance(String username);

    // Find top 3 users based on distance run during the current week
    @Query("SELECT u.username, SUM(r.totalDistance) as totalDistance FROM Run r " +
           "JOIN r.user u WHERE r.date BETWEEN :startOfWeek AND :endOfWeek " +
           "GROUP BY u.username ORDER BY SUM(r.totalDistance) DESC")
    List<Object[]> findTop3UsersByWeeklyDistance(LocalDate startOfWeek, LocalDate endOfWeek);
}
