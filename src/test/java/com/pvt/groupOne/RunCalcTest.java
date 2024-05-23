package com.pvt.groupOne;

import com.pvt.groupOne.controller.RunDataCalcController;
import com.pvt.groupOne.model.Run;
import com.pvt.groupOne.model.RunnerGroup;
import com.pvt.groupOne.model.User;
import com.pvt.groupOne.repository.RunRepository;
import com.pvt.groupOne.repository.RunnerGroupRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class RunCalcTest {

    private MockMvc mockMvc;

    @Mock
    private RunnerGroupRepository groupRepository;

    @Mock
    private RunRepository runRepository;

    @InjectMocks
    private RunDataCalcController runDataCalcController;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(runDataCalcController).build();
    }

    @Test
    public void testGetTeamTotalHoursWithValidGroupName() throws Exception {
        // Given
        String validGroupName = "TeamA";
        RunnerGroup runnerGroup = createMockRunnerGroup();
        when(groupRepository.findGroupByTeamName(validGroupName)).thenReturn(runnerGroup);

        // Mock the runRepository response
        List<String> runTimesForUser1 = List.of("01:30:00", "00:45:00");
        List<String> runTimesForUser2 = List.of("02:00:00");
        when(runRepository.getAllRunTimeByUser("john_doe")).thenReturn(runTimesForUser1);
        when(runRepository.getAllRunTimeByUser("jane_doe")).thenReturn(runTimesForUser2);

        // When
        ResultActions result = mockMvc.perform(get("/run/get-team-total-hours/" + validGroupName)
                .contentType(MediaType.APPLICATION_JSON));

        // Then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.RunTime").value(4)); // 1:30 + 0:45 + 2:00 = 4:15 = 4 hours
    }

    @Test
    public void testGetTeamTotalDistanceWithValidGroupName() throws Exception {
        // Given
        String validGroupName = "TeamA";
        RunnerGroup runnerGroup = createMockRunnerGroup();
        when(groupRepository.findGroupByTeamName(validGroupName)).thenReturn(runnerGroup);

        // Mock the runRepository response
        List<Double> runDistancesForUser1 = List.of(10.0, 15.5);
        List<Double> runDistancesForUser2 = List.of(20.0);
        when(runRepository.getAllRunDistanceByUser("john_doe")).thenReturn(runDistancesForUser1);
        when(runRepository.getAllRunDistanceByUser("jane_doe")).thenReturn(runDistancesForUser2);

        // When
        ResultActions result = mockMvc.perform(get("/run/get-team-total-distance/" + validGroupName)
                .contentType(MediaType.APPLICATION_JSON));

        // Then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.totaldistance").value(45)); // 10 + 15.5 + 20 = 45.5 rounded to 45
    }

    private RunnerGroup createMockRunnerGroup() {
        RunnerGroup runnerGroup = new RunnerGroup();
        runnerGroup.setTeamName("TeamA");
        runnerGroup.setCompanyName("CompanyName");

        User user1 = new User("john_doe", "password", "john.doe@example.com", "CompanyName");
        User user2 = new User("jane_doe", "password", "jane.doe@example.com", "CompanyName");

        List<User> users = new ArrayList<>();
        users.add(user1);
        users.add(user2);

        runnerGroup.setUsers(users);
        return runnerGroup;
    }
}
