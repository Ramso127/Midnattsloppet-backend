package com.pvt.groupOne.repository;

import com.pvt.groupOne.model.RunnerGroup;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface RunnerGroupRepository extends CrudRepository<RunnerGroup, Integer> {
       boolean existsByTeamName(String groupName);

       boolean existsByInviteCode(String inviteCode);

       RunnerGroup findGroupByInviteCode(String inviteCode);

       RunnerGroup findGroupByTeamName(String groupName);

       @Query("SELECT r.user.username, SUM(r.totalDistance) FROM Run r " +
                     "WHERE r.user.runnerGroup.groupId = :groupId " +
                     "GROUP BY r.user.username ORDER BY SUM(r.totalDistance) DESC")
       List<Object[]> findTeamMembersByDistance(Integer groupId);

       @Query("SELECT r.user.runnerGroup.groupId, r.user.runnerGroup.teamName, SUM(r.totalDistance) " +
                     "FROM Run r " +
                     "WHERE r.user.runnerGroup.groupId IS NOT NULL " +
                     "GROUP BY r.user.runnerGroup.groupId, r.user.runnerGroup.teamName " +
                     "ORDER BY SUM(r.totalDistance) DESC")
       List<Object[]> findTopGroupsByTotalDistance();

       @Query("SELECT r.user.runnerGroup.groupId, r.user.runnerGroup.teamName, SUM(r.totalDistance) " +
                     "FROM Run r " +
                     "WHERE r.user.runnerGroup.groupId IS NOT NULL " +
                     "GROUP BY r.user.runnerGroup.groupId, r.user.runnerGroup.teamName " +
                     "ORDER BY SUM(r.totalDistance) DESC " +
                     "LIMIT 3")
       List<Object[]> findTop3GroupsByTotalDistance();

       @Query("SELECT r.user.runnerGroup.groupId, r.user.runnerGroup.teamName, SUM(r.totalDistance) " +
                     "FROM Run r " +
                     "WHERE r.user.runnerGroup.groupId IS NOT NULL " +
                     "AND r.date BETWEEN :startDate AND :endDate " +
                     "GROUP BY r.user.runnerGroup.groupId, r.user.runnerGroup.teamName " +
                     "ORDER BY SUM(r.totalDistance) DESC")
       List<Object[]> findTopGroupsByDistance(@Param("startDate") LocalDate startDate,
                     @Param("endDate") LocalDate endDate);

       @Query("SELECT COUNT(DISTINCT r.teamName) FROM RunnerGroup r")
       int countDistinctTeams();

       @Query("SELECT g FROM RunnerGroup g JOIN g.users u GROUP BY g ORDER BY SUM(u.points) DESC")
       List<RunnerGroup> findGroupsOrderByMembersTotalPoints();

       @Query("SELECT g FROM RunnerGroup g ORDER BY g.points DESC")
       List<RunnerGroup> findAllGroupsOrderByPoints();

}
