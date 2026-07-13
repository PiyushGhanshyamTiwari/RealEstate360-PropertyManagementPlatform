package com.cts.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import com.cts.dto.*;
import com.cts.entity.User;
import com.cts.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController controller;

    private MockMvc mockMvc;

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    
    @Test
    void testRegisterUser() throws Exception {

        RegistrationInputDTO input = new RegistrationInputDTO();
        input.setEmailId("test@mail.com");

        when(userService.registerUser(any()))
                .thenReturn(new RegistrationOutputDTO());

        mockMvc.perform(post("/api/v1/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isCreated());

        verify(userService).registerUser(any());
    }

    
    @Test
    void testGetAllUsers() throws Exception {

        when(userService.getAllUsers())
                .thenReturn(List.of(new User()));

        mockMvc.perform(get("/api/v1/user/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));

        verify(userService).getAllUsers();
    }

    
    @Test
    void testGetAllUsersEmpty() throws Exception {

        when(userService.getAllUsers())
                .thenReturn(List.of());

        mockMvc.perform(get("/api/v1/user/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    
    @Test
    void testUserLoginSuccess() throws Exception {

        LoginDTO login = new LoginDTO();
        login.setEmailId("test@mail.com");
        login.setPassword("pwd");

        LoginResponseDTO response = new LoginResponseDTO();

        when(userService.userLogin(any()))
                .thenReturn(response);

        mockMvc.perform(post("/api/v1/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isOk());

        verify(userService).userLogin(any());
    }

    
    @Test
    void testUserLoginFailure() throws Exception {

        LoginDTO login = new LoginDTO();
        login.setEmailId("test@mail.com");
        login.setPassword("wrong");

        when(userService.userLogin(any()))
                .thenReturn(null);

        mockMvc.perform(post("/api/v1/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Login Failed"));
    }
}