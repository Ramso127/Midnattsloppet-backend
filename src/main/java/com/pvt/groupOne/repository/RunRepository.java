package com.pvt.groupOne.repository;

import com.pvt.groupOne.model.Run;
import com.pvt.groupOne.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

public interface RunRepository extends JpaRepository<Run, Long> {

    @Query("SELECT r.totalTime FROM Run r WHERE r.id = :id")
    String findRunTimeById(@Param("id") Long id);

    @Query("SELECT r.totalDistance FROM Run r WHERE r.id = :id")
    double findRunDistanceById(@Param("id") Long id);

    @Query("SELECT r.date FROM Run r WHERE r.id = :id")
    LocalDate findRunDateById(@Param("id") Long id);

    @Query("SELECT r.user FROM Run r WHERE r.id = :id")
    User findUserFromRunById(@Param("id") Long id);

    // Find top 3 runs for a user ordered by distance
    @Query("SELECT r FROM Run r WHERE r.user.username = :username ORDER BY r.totalDistance DESC")
    List<Run> findTop3RunsByDistance(String username);

    // Find top 3 users based on distance run during the current week
    @Query("SELECT u.username, SUM(r.totalDistance) as totalDistance FROM Run r " +
           "JOIN r.user u WHERE r.date BETWEEN :startOfWeek AND :endOfWeek " +
           "GROUP BY u.username ORDER BY SUM(r.totalDistance) DESC")
    List<Object[]> findTop3UsersByWeeklyDistance(LocalDate startOfWeek, LocalDate endOfWeek);
}
