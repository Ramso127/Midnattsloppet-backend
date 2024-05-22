package com.pvt.groupOne;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pvt.groupOne.Service.RunnerGroupService;
import com.pvt.groupOne.Service.UserService;
import com.pvt.groupOne.model.GroupRequest;
import com.pvt.groupOne.model.PasswordEncryption;
import com.pvt.groupOne.model.RunnerGroup;
import com.pvt.groupOne.model.StravaUser;
import com.pvt.groupOne.model.User;
import com.pvt.groupOne.model.UserRequest;
import com.pvt.groupOne.repository.AccountRepository;
import com.pvt.groupOne.repository.RunnerGroupRepository;
import com.pvt.groupOne.repository.StravaUserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AccountRepository accountRepository;

    @MockBean
    private RunnerGroupRepository groupRepository;

    @MockBean
    private RunnerGroupService runnerGroupService;

    @MockBean
    private PasswordEncryption passwordEncryption;

    @MockBean
    private StravaUserRepository stravaUserRepository;

    @MockBean
    private UserService userService;

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        when(passwordEncryption.passwordEncoder()).thenReturn(new BCryptPasswordEncoder());
        when(accountRepository.existsByUsername(any(String.class))).thenReturn(false);
        when(accountRepository.existsByEmail(any(String.class))).thenReturn(false);
    }

    @Test
    public void testAddUserSuccess() throws Exception {
        UserRequest userRequest = new UserRequest("john_doe", "password", "john.doe@example.com", "CompanyName");
        String userRequestJson = objectMapper.writeValueAsString(userRequest);

        mockMvc.perform(post("/controller/adduser")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userRequestJson))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void testAddUserUsernameExists() throws Exception {
        UserRequest userRequest = new UserRequest("john_doe", "password", "john.doe@example.com", "CompanyName");
        String userRequestJson = objectMapper.writeValueAsString(userRequest);

        when(accountRepository.existsByUsername("john_doe")).thenReturn(true);

        mockMvc.perform(post("/controller/adduser")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userRequestJson))
                .andExpect(status().isBadRequest())
                .andExpect(content().json("{\"message\": \"Username already exists\"}"));
    }

    @Test
    public void testAddUserEmailExists() throws Exception {
        UserRequest userRequest = new UserRequest("john_doe", "password", "john.doe@example.com", "CompanyName");
        String userRequestJson = objectMapper.writeValueAsString(userRequest);

        when(accountRepository.existsByEmail("john.doe@example.com")).thenReturn(true);

        mockMvc.perform(post("/controller/adduser")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userRequestJson))
                .andExpect(status().isBadRequest())
                .andExpect(content().json("{\"message\": \"Email already exists\"}"));
    }

    @Test
    public void testLoginSuccess() throws Exception {
        String username = "john_doe";
        String password = "password";
        User user = new User(username, password, "john.doe@example.com", "CompanyName");

        when(userService.authenticateUser(username, password)).thenReturn(true);
        when(accountRepository.findByUsername(username)).thenReturn(user);

        mockMvc.perform(get("/controller/login/" + username + "/" + password))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(user)));
    }

    @Test
    public void testLoginUnauthorized() throws Exception {
        String username = "john_doe";
        String password = "password";

        when(userService.authenticateUser(username, password)).thenReturn(false);

        mockMvc.perform(get("/controller/login/" + username + "/" + password))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Invalid username or password"));
    }

    @Test
    public void testLoginInternalServerError() throws Exception {
        String username = "john_doe";
        String password = "password";

        when(userService.authenticateUser(username, password)).thenThrow(new RuntimeException());

        mockMvc.perform(get("/controller/login/" + username + "/" + password))
                .andExpect(status().isInternalServerError());
    }

        @Test
    public void testAddGroupSuccess() throws Exception {
        String teamName = "TeamA";
        String username = "john_doe";
        String companyName = "CompanyName";
        GroupRequest groupRequest = new GroupRequest(teamName, username);
        String groupRequestJson = objectMapper.writeValueAsString(groupRequest);
        User user = new User(username, "password", "john.doe@example.com", companyName);
        RunnerGroup runnerGroup = new RunnerGroup();

        runnerGroup.setTeamName(teamName);
        runnerGroup.setCompanyName(companyName);
        runnerGroup.addUser(user);

        when(accountRepository.findByUsername(username)).thenReturn(user);
        when(groupRepository.existsByTeamName(teamName)).thenReturn(false);
        when(runnerGroupService.createRunnerGroup(teamName, user, companyName)).thenReturn(runnerGroup);

        mockMvc.perform(post("/controller/addgroup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(groupRequestJson))
                .andExpect(status().isCreated())
                .andExpect(content().json(objectMapper.writeValueAsString(runnerGroup)));
    }

    @Test
    public void testAddGroupAlreadyExists() throws Exception {
        String teamName = "TeamA";
        String username = "john_doe";
        GroupRequest groupRequest = new GroupRequest(teamName, username);
        String groupRequestJson = objectMapper.writeValueAsString(groupRequest);
        User user = new User(username, "password", "john.doe@example.com", "CompanyName");

        when(accountRepository.findByUsername(username)).thenReturn(user);
        when(groupRepository.existsByTeamName(teamName)).thenReturn(true);

        mockMvc.perform(post("/controller/addgroup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(groupRequestJson))
                .andExpect(status().isBadRequest())
                .andExpect(content().json("{\"message\": \"Group name already exists\"}"));
    }

    @Test
    public void testFetchStravaRunsWithoutAccount() throws Exception {
        String username = "testuser";

        when(stravaUserRepository.findByUser_Username(username)).thenReturn(null);

        mockMvc.perform(put("/controller/fetchruns")
                .param("username", username))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("ERROR: No Strava account connected to user " + username));
    }
}