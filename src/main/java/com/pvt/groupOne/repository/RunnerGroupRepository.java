package com.pvt.groupOne.repository;

import com.pvt.groupOne.model.Run;
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

       @Query(value = "SELECT rg.team_name, SUM(r.total_distance) as metric FROM run r " +
                     "JOIN user u ON r.user_id = u.user_name " +
                     "JOIN runner_group rg ON u.runner_group_id = rg.group_id " +
                     "WHERE r.date BETWEEN :startDate AND :endDate " +
                     "AND (TIME_TO_SEC(r.total_time) / 60) / r.total_distance <= 8 " +
                     "GROUP BY rg.team_name ORDER BY metric DESC", nativeQuery = true)
       List<Object[]> findGroupsByFurthestDistance(@Param("startDate") LocalDate startDate,
                     @Param("endDate") LocalDate endDate);

       @Query(value = "SELECT rg.team_name, COUNT(r.id) as metric FROM run r " +
                     "JOIN user u ON r.user_id = u.user_name " +
                     "JOIN runner_group rg ON u.runner_group_id = rg.group_id " +
                     "WHERE r.date BETWEEN :startDate AND :endDate " +
                     "AND r.total_distance >= 2 " +
                     "GROUP BY rg.team_name ORDER BY metric DESC", nativeQuery = true)
       List<Object[]> findGroupsByMostRuns(@Param("startDate") LocalDate startDate,
                     @Param("endDate") LocalDate endDate);

       @Query(value = "SELECT rg.team_name, SUM(sub.total_distance) as metric " +
                     "FROM (SELECT u.user_name, MAX(r.total_distance) as total_distance " +
                     "FROM run r JOIN user u ON r.user_id = u.user_name " +
                     "WHERE (TIME_TO_SEC(r.total_time) / 60) / r.total_distance <= 8 " +
                     "AND r.date BETWEEN :startDate AND :endDate " +
                     "GROUP BY u.user_name) as sub " +
                     "JOIN user u ON sub.user_name = u.user_name " +
                     "JOIN runner_group rg ON u.runner_group_id = rg.group_id " +
                     "GROUP BY rg.team_name ORDER BY metric DESC", nativeQuery = true)
       List<Object[]> findGroupsByFurthestRunPerMember(@Param("startDate") LocalDate startDate,
                     @Param("endDate") LocalDate endDate);

       @Query(value = "SELECT rg.team_name, " +
                     "AVG((TIME_TO_SEC(r.total_time) / 60) / r.total_distance) as average_pace " +
                     "FROM run r " +
                     "JOIN user u ON r.user_id = u.user_name " +
                     "JOIN runner_group rg ON u.runner_group_id = rg.group_id " +
                     "WHERE r.date BETWEEN :startDate AND :endDate " +
                     "AND r.total_distance >= 2 " +
                     "GROUP BY rg.team_name ORDER BY average_pace ASC", nativeQuery = true)
       List<Object[]> findGroupsByHighestAveragePace(@Param("startDate") LocalDate startDate,
                     @Param("endDate") LocalDate endDate);

       @Query(value = "SELECT rg.team_name, COUNT(r.id) as metric FROM run r " +
                     "JOIN user u ON r.user_id = u.user_name " +
                     "JOIN runner_group rg ON u.runner_group_id = rg.group_id " +
                     "WHERE r.total_distance >= 5 AND r.date BETWEEN :startDate AND :endDate " +
                     "GROUP BY rg.team_name ORDER BY metric DESC", nativeQuery = true)
       List<Object[]> findGroupsByMostLongRuns(@Param("startDate") LocalDate startDate,
                     @Param("endDate") LocalDate endDate);

       @Query(value = "SELECT rg.team_name, COUNT(r.id) as metric FROM run r " +
                     "JOIN user u ON r.user_id = u.user_name " +
                     "JOIN runner_group rg ON u.runner_group_id = rg.group_id " +
                     "WHERE (TIME_TO_SEC(r.total_time) / 60) / r.total_distance <= 6 " +
                     "AND r.date BETWEEN :startDate AND :endDate " +
                     "GROUP BY rg.team_name ORDER BY metric DESC", nativeQuery = true)
       List<Object[]> findGroupsByTotalNumberOfFasterRuns(@Param("startDate") LocalDate startDate,
                     @Param("endDate") LocalDate endDate);

       @Query("SELECT r FROM Run r WHERE r.user.runnerGroup = (SELECT u.runnerGroup FROM User u WHERE u.username = :username) ORDER BY r.date DESC")
       List<Run> findLatestRunsByTeamMemberUsername(@Param("username") String username);

       // A specific groups challenge stats
       // Challenge 1: Furthest Distance with Minimum Pace
       @Query(value = "SELECT SUM(r.total_distance) FROM run r " +
                     "JOIN user u ON r.user_id = u.user_name " +
                     "WHERE u.runner_group_id = :groupId AND r.date BETWEEN :startDate AND :endDate " +
                     "AND (TIME_TO_SEC(r.total_time) / 60 / r.total_distance) <= 8", nativeQuery = true)
       Double findGroupDistanceForChallenge(@Param("groupId") Integer groupId, @Param("startDate") LocalDate startDate,
                     @Param("endDate") LocalDate endDate);

       // Challenge 2: Most Runs with Minimum Distance Per Run
       @Query(value = "SELECT COUNT(r.id) FROM run r " +
                     "JOIN user u ON r.user_id = u.user_name " +
                     "WHERE u.runner_group_id = :groupId AND r.date BETWEEN :startDate AND :endDate " +
                     "AND r.total_distance >= 2", nativeQuery = true)
       Double findGroupRunsForChallenge(@Param("groupId") Integer groupId, @Param("startDate") LocalDate startDate,
                     @Param("endDate") LocalDate endDate);

       // Challenge 3: Furthest Run Per Member
       @Query(value = "SELECT SUM(max_distance) FROM (SELECT MAX(r.total_distance) as max_distance " +
                     "FROM run r JOIN user u ON r.user_id = u.user_name " +
                     "WHERE u.runner_group_id = :groupId AND r.date BETWEEN :startDate AND :endDate " +
                     "AND (TIME_TO_SEC(r.total_time) / 60 / r.total_distance) <= 8 GROUP BY u.user_name) as max_distances", nativeQuery = true)
       Double findGroupFurthestRunPerMemberForChallenge(@Param("groupId") Integer groupId,
                     @Param("startDate") LocalDate startDate,
                     @Param("endDate") LocalDate endDate);

       // Challenge 4: Lowest Average Pace with Minimum Distance
       @Query(value = "SELECT AVG(TIME_TO_SEC(r.total_time) / 60 / r.total_distance) FROM run r " +
                     "JOIN user u ON r.user_id = u.user_name " +
                     "WHERE u.runner_group_id = :groupId AND r.date BETWEEN :startDate AND :endDate " +
                     "AND r.total_distance >= 2", nativeQuery = true)
       Double findGroupAveragePaceForChallenge(@Param("groupId") Integer groupId,
                     @Param("startDate") LocalDate startDate,
                     @Param("endDate") LocalDate endDate);

       // Challenge 5: Most Long Runs
       @Query(value = "SELECT COUNT(r.id) FROM run r " +
                     "JOIN user u ON r.user_id = u.user_name " +
                     "WHERE u.runner_group_id = :groupId AND r.date BETWEEN :startDate AND :endDate " +
                     "AND r.total_distance >= 5", nativeQuery = true)
       Double findGroupLongRunsForChallenge(@Param("groupId") Integer groupId, @Param("startDate") LocalDate startDate,
                     @Param("endDate") LocalDate endDate);

       // Challenge 6: Total Number of Faster Runs
       @Query(value = "SELECT COUNT(r.id) FROM run r " +
                     "JOIN user u ON r.user_id = u.user_name " +
                     "WHERE u.runner_group_id = :groupId AND r.date BETWEEN :startDate AND :endDate " +
                     "AND (TIME_TO_SEC(r.total_time) / 60 / r.total_distance) <= 6", nativeQuery = true)
       Double findGroupFasterRunsForChallenge(@Param("groupId") Integer groupId,
                     @Param("startDate") LocalDate startDate,
                     @Param("endDate") LocalDate endDate);

       // User Challenge Contribution
       // Challenge 1: User's Furthest Distance with Minimum Pace
       @Query(value = "SELECT SUM(r.total_distance) FROM run r " +
                     "WHERE r.user_id = :userId AND r.date BETWEEN :startDate AND :endDate " +
                     "AND (TIME_TO_SEC(r.total_time) / 60 / r.total_distance) <= 8", nativeQuery = true)
       Double findUserDistanceForWeek(@Param("userId") String userId, @Param("startDate") LocalDate startDate,
                     @Param("endDate") LocalDate endDate);

       // Challenge 2: Most Runs by User with Minimum Distance Per Run
       @Query(value = "SELECT COUNT(r.id) FROM run r " +
                     "WHERE r.user_id = :userId AND r.date BETWEEN :startDate AND :endDate " +
                     "AND r.total_distance >= 2", nativeQuery = true)
       Double countUserRuns(@Param("userId") String userId, @Param("startDate") LocalDate startDate,
                     @Param("endDate") LocalDate endDate);

       // Challenge 3: User's Longest Run
       @Query(value = "SELECT MAX(r.total_distance) FROM run r " +
                     "WHERE r.user_id = :userId AND r.date BETWEEN :startDate AND :endDate", nativeQuery = true)
       Double findUserLongestRun(@Param("userId") String userId, @Param("startDate") LocalDate startDate,
                     @Param("endDate") LocalDate endDate);

       // Challenge 4: Lowest Average Pace by User with Minimum Distance
       @Query(value = "SELECT AVG(TIME_TO_SEC(r.total_time) / 60 / r.total_distance) FROM run r " +
                     "WHERE r.user_id = :userId AND r.date BETWEEN :startDate AND :endDate " +
                     "AND r.total_distance >= 2", nativeQuery = true)
       Double calculateUserAveragePace(@Param("userId") String userId, @Param("startDate") LocalDate startDate,
                     @Param("endDate") LocalDate endDate);

       // Challenge 5: Most Long Runs by User
       @Query(value = "SELECT COUNT(r.id) FROM run r " +
                     "WHERE r.user_id = :userId AND r.date BETWEEN :startDate AND :endDate " +
                     "AND r.total_distance >= 5", nativeQuery = true)
       Double countUserLongRuns(@Param("userId") String userId, @Param("startDate") LocalDate startDate,
                     @Param("endDate") LocalDate endDate);

       // Challenge 6: Total Number of Faster Runs by User
       @Query(value = "SELECT COUNT(r.id) FROM run r " +
                     "WHERE r.user_id = :userId AND r.date BETWEEN :startDate AND :endDate " +
                     "AND (TIME_TO_SEC(r.total_time) / 60 / r.total_distance) <= 6", nativeQuery = true)
       Double countUserFasterRuns(@Param("userId") String userId, @Param("startDate") LocalDate startDate,
                     @Param("endDate") LocalDate endDate);

}
