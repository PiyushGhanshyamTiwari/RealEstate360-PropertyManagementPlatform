package com.cts.controller;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.cts.dto.LoginDTO;
import com.cts.dto.LoginResponseDTO;
import com.cts.dto.RegistrationInputDTO;
import com.cts.dto.RegistrationOutputDTO;
import com.cts.entity.User;
import com.cts.exception.UserIdNotFoundException;
import com.cts.serviceimpl.UserServiceImpl;

/**
 * Unit tests for {@link UserController}.
 *
 * <p>Mocks the concrete {@link UserServiceImpl} and injects it into the controller. Covers
 * registration, listing the {@link User} entities, login bean-validation on {@code @Valid LoginDTO},
 * the mapped 404 ({@link UserNotFoundException}) login branch, and surfacing of an unmapped
 * registration failure.
 */
@ExtendWith(MockitoExtension.class)
class UserControllerTest extends AbstractControllerTest {

    @Mock
    private UserServiceImpl userService;

    @InjectMocks
    private UserController userController;

    private MockMvc mockMvc;

    private static final String BASE_URL = "/api/v1/user";

    @BeforeEach
    void setUp() {
        mockMvc = buildMockMvc(userController);
    }

    private RegistrationInputDTO validRegistration() {
        return RegistrationInputDTO.builder()
                .userName("Jane Doe")
                .emailId("jane@example.com")
                .phone("9876543210")
                .role("tenant")
                .password("secret")
                .build();
    }

    private RegistrationOutputDTO sampleRegistrationOutput() {
        return RegistrationOutputDTO.builder()
                .userId(1)
                .userName("Jane Doe")
                .emailId("jane@example.com")
                .phone(9876543210L)
                .role("tenant")
                .registeredOn(LocalDate.now())
                .build();
    }

    private User sampleUser() {
        return User.builder()
                .userId(1)
                .userName("Jane Doe")
                .emailId("jane@example.com")
                .phone(9876543210L)
                .role("tenant")
                .password("secret")
                .registeredOn(LocalDate.now())
                .build();
    }

    private LoginDTO validLogin() {
        return LoginDTO.builder()
                .emailId("jane@example.com")
                .password("secret")
                .build();
    }

    private LoginResponseDTO sampleLoginResponse() {
        return LoginResponseDTO.builder()
                .userId(1)
                .userName("Jane Doe")
                .emailId("jane@example.com")
                .phone(9876543210L)
                .role("tenant")
                .build();
    }

    @Test
    @DisplayName("POST register -> 201 with created user")
    void shouldReturn201WhenUserRegistered() throws Exception {
        // Arrange
        when(userService.registerUser(any(RegistrationInputDTO.class)))
                .thenReturn(sampleRegistrationOutput());

        // Act & Assert
        mockMvc.perform(post(BASE_URL + "/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(validRegistration())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.emailId").value("jane@example.com"));

        verify(userService, times(1)).registerUser(any(RegistrationInputDTO.class));
    }

    @Test
    @DisplayName("POST register with duplicate email -> failure surfaced")
    void shouldSurfaceFailureWhenEmailAlreadyExists() throws Exception {
        // Arrange
        when(userService.registerUser(any(RegistrationInputDTO.class)))
                .thenThrow(new RuntimeException("Email already exists"));

        // Act & Assert
        assertRequestFailsWith("Email already exists", () ->
                mockMvc.perform(post(BASE_URL + "/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(validRegistration()))));

        verify(userService, times(1)).registerUser(any(RegistrationInputDTO.class));
    }

    @Test
    @DisplayName("GET all users -> 200 with list")
    void shouldReturn200WhenUsersExist() throws Exception {
        // Arrange
        when(userService.getAllUsers()).thenReturn(List.of(sampleUser()));

        // Act & Assert
        mockMvc.perform(get(BASE_URL + "/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].userId").value(1))
                .andExpect(jsonPath("$[0].fullName").value("Jane Doe"))
                .andExpect(jsonPath("$[0].emailId").value("jane@example.com"));

        verify(userService, times(1)).getAllUsers();
    }

    @Test
    @DisplayName("GET all users when none -> 200 with empty list")
    void shouldReturn200WithEmptyListWhenNoUsers() throws Exception {
        // Arrange
        when(userService.getAllUsers()).thenReturn(Collections.emptyList());

        // Act & Assert
        mockMvc.perform(get(BASE_URL + "/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));

        verify(userService, times(1)).getAllUsers();
    }

    @Test
    @DisplayName("POST login -> 200 with login response")
    void shouldReturn200WhenLoginSucceeds() throws Exception {
        // Arrange
        when(userService.userLogin(any(LoginDTO.class))).thenReturn(sampleLoginResponse());

        // Act & Assert
        mockMvc.perform(post(BASE_URL + "/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(validLogin())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.role").value("tenant"));

        verify(userService, times(1)).userLogin(any(LoginDTO.class));
    }

    @Test
    @DisplayName("POST login with invalid credentials payload -> 400 (validation) and service not invoked")
    void shouldReturn400WhenLoginPayloadInvalid() throws Exception {
        // Arrange
        LoginDTO invalid = LoginDTO.builder().emailId("not-an-email").password("x").build();

        // Act & Assert
        mockMvc.perform(post(BASE_URL + "/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(invalid)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(userService);
    }

    @Test
    @DisplayName("POST login for unknown user -> 404 Not Found")
    void shouldReturn404WhenUserNotFound() throws Exception {
        // Arrange
        when(userService.userLogin(any(LoginDTO.class)))
                .thenThrow(new UserIdNotFoundException("User not found"));

        // Act & Assert
        mockMvc.perform(post(BASE_URL + "/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(validLogin())))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("User not found")));

        verify(userService, times(1)).userLogin(any(LoginDTO.class));
    }
}
