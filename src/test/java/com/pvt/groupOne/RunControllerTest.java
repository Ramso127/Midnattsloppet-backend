package com.pvt.groupOne;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pvt.groupOne.Service.RunService;
import com.pvt.groupOne.controller.MainController;
import com.pvt.groupOne.model.Run;
import com.pvt.groupOne.model.RunRequest;
import com.pvt.groupOne.model.RunnerGroup;
import com.pvt.groupOne.model.User;
import com.pvt.groupOne.repository.AccountRepository;
import com.pvt.groupOne.repository.RunRepository;
import com.pvt.groupOne.repository.RunnerGroupRepository;

public class RunControllerTest {

    private MockMvc mockMvc;

    @Mock
    private RunService runService;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private RunnerGroupRepository groupRepository;

    @Mock
    private RunRepository runRepository;

    @InjectMocks
    private MainController mainController;
    

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(mainController).build();  

    }

    @Test
    public void testAddRunSuccess() throws Exception {
        // Mock request data
        RunRequest runRequest = new RunRequest();
        runRequest.setUsername("john_doe");
        runRequest.setDate("2024-05-22");
        runRequest.setMinutes(60);
        runRequest.setSeconds(30);
        runRequest.setTotaldistance(10.5);

        // Mock user
        User user = new User("john_doe", "password", "john.doe@example.com", "CompanyName");
        when(accountRepository.findByUsername("john_doe")).thenReturn(user);


        doNothing().when(runService).saveRun(any(Run.class));        // Perform the request
        
        mockMvc.perform(post("/controller/addrun")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(runRequest)))
                .andExpect(status().isOk());
    }

    @Test
    void testAddRunWithInvalidDateFormat() throws Exception {
        // Mocking an existing user
        String username = "testUser";
        User user = new User(username, "password", "test@example.com", "Test Company");
        RunRequest runRequest = new RunRequest();
        runRequest.setUsername(username);
        runRequest.setDate("2024/05/22");
        runRequest.setMinutes(30);
        runRequest.setSeconds(15);
        runRequest.setTotaldistance(5.0);
        when(accountRepository.findByUsername(username)).thenReturn(user);

        // Perform the POST request with an invalid date format and verify the response
        mockMvc.perform(post("/controller/addrun")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(runRequest)))
                .andExpect(status().isBadRequest());
    }

    // Helper method to convert object to JSON string
    private String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

        @Test
    public void testGetTeamMembersWithValidGroupName() throws Exception {
        // Given
        String validGroupName = "TeamA";
        // Mock the repository response to return a valid group
        when(groupRepository.findGroupByTeamName(validGroupName)).thenReturn(createMockRunnerGroup());
        List<Double> list = new ArrayList<>();
        list.add(100.25);
        when(runRepository.getAllRunDistanceByUser("john_doe")).thenReturn(list);
        // When
        ResultActions result = mockMvc.perform(get("/controller/getteammembers/" + validGroupName)
                                    .contentType(MediaType.APPLICATION_JSON));

        // Then
        result.andExpect(status().isOk())
              .andExpect(jsonPath("$.data[0].username").value("john_doe"))
              .andExpect(jsonPath("$.data[0].distance").value(100.25));
    }



    // Helper method to create a mock RunnerGroup
    private RunnerGroup createMockRunnerGroup() {
        RunnerGroup runnerGroup = new RunnerGroup();
        User user = new User("john_doe", "password", "john.doe@example.com", "CompanyName");
        runnerGroup.addUser(user);
        return runnerGroup;
    }
    

    @Test
    public void testGetTeamMembersWithValidGroupNameInOrder() throws Exception {
        // Given
        String validGroupName = "TeamA";
        RunnerGroup runnerGroup = new RunnerGroup();
        User user = new User("john_doe", "password", "john.doe@example.com", "CompanyName");
        User user2 = new User("jane_doe", "password1", "jane.doe@example.com", "CompanyName");
        User user3 = new User("sven_doe", "password1", "jane.doe@example.com", "CompanyName");
        runnerGroup.addUser(user3);
        runnerGroup.addUser(user);
        runnerGroup.addUser(user2);
        when(groupRepository.findGroupByTeamName(validGroupName)).thenReturn(runnerGroup);
    
        // Mock the runRepository response
        when(runRepository.getAllRunDistanceByUser("john_doe")).thenReturn(List.of(10.0, 15.5));
        when(runRepository.getAllRunDistanceByUser("jane_doe")).thenReturn(List.of(20.0));
        when(runRepository.getAllRunDistanceByUser("sven_doe")).thenReturn(List.of(15.0));
        
        // When
        ResultActions result = mockMvc.perform(get("/controller/getteammembers/" + validGroupName)
                .contentType(MediaType.APPLICATION_JSON));
    
        // Then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.data", Matchers.hasSize(3))) // Ensure there are only two users
                .andExpect(jsonPath("$.data[2].username").value("sven_doe"))
                .andExpect(jsonPath("$.data[2].distance").value(15.0))
                .andExpect(jsonPath("$.data[1].username").value("jane_doe"))
                .andExpect(jsonPath("$.data[1].distance").value(20.0))
                .andExpect(jsonPath("$.data[0].username").value("john_doe"))
                .andExpect(jsonPath("$.data[0].distance").value(25.5))
                .andExpect(jsonPath("$.data[1].distance").isNumber()) // Ensure distance value is a number
                .andExpect(jsonPath("$.data[0].distance").isNumber());
    }
    

}
