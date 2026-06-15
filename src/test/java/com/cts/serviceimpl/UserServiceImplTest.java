package com.cts.serviceimpl;

import com.cts.dto.LoginDTO;
import com.cts.dto.LoginResponseDTO;
import com.cts.dto.RegistrationInputDTO;
import com.cts.dto.RegistrationOutputDTO;
import com.cts.entity.User;
import com.cts.exception.UserIdNotFoundException;
import com.cts.mapper.UserRegistrationMapper;
import com.cts.repository.UserRepository;
import com.cts.serviceimpl.UserServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserRegistrationMapper mapper;

    @InjectMocks
    private UserServiceImpl service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // Positive: registerUser succeeds
    @Test
    void testRegisterUser_Success() {
        RegistrationInputDTO input = new RegistrationInputDTO();
        input.setEmailId("test@example.com");
        input.setPassword("plain123");

        User user = new User();
        user.setEmailId("test@example.com");
        user.setPassword("plain123");

        User savedUser = new User();
        savedUser.setUserId(1);
        savedUser.setEmailId("test@example.com");
        savedUser.setPassword("encoded123");

        RegistrationOutputDTO outputDto = new RegistrationOutputDTO();
        outputDto.setUserId(1);

        when(userRepository.existsByEmailId("test@example.com")).thenReturn(false);
        when(mapper.convertToUser(input)).thenReturn(user);
        when(passwordEncoder.encode("plain123")).thenReturn("encoded123");
        when(userRepository.save(user)).thenReturn(savedUser);
        when(mapper.convertToUserResponseDTO(savedUser)).thenReturn(outputDto);

        RegistrationOutputDTO result = service.registerUser(input);

        assertNotNull(result);
        assertEquals(1, result.getUserId());
    }

    // Negative: registerUser email already exists
    @Test
    void testRegisterUser_EmailExists() {
        RegistrationInputDTO input = new RegistrationInputDTO();
        input.setEmailId("duplicate@example.com");

        when(userRepository.existsByEmailId("duplicate@example.com")).thenReturn(true);

        assertThrows(RuntimeException.class,
                () -> service.registerUser(input));
    }

    // Positive: getAllUsers
    @Test
    void testGetAllUsers() {
        User user1 = new User();
        user1.setUserId(1);
        User user2 = new User();
        user2.setUserId(2);

        when(userRepository.findAll()).thenReturn(Arrays.asList(user1, user2));

        List<User> result = service.getAllUsers();

        assertEquals(2, result.size());
    }

    // Positive: userLogin succeeds
    @Test
    void testUserLogin_Success() {
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setEmailId("test@example.com");
        loginDTO.setPassword("plain123");

        User user = new User();
        user.setUserId(1);
        user.setUserName("Test User");
        user.setEmailId("test@example.com");
        user.setPhone(1234567890);
        user.setRole("ADMIN");
        user.setPassword("encoded123");

        when(userRepository.findUserByEmail("test@example.com")).thenReturn(user);
        when(passwordEncoder.matches("plain123", "encoded123")).thenReturn(true);

        LoginResponseDTO result = service.userLogin(loginDTO);

        assertNotNull(result);
        assertEquals("Test User", result.getUserName());
        assertEquals("ADMIN", result.getRole());
    }

    // Negative: userLogin user not found
    @Test
    void testUserLogin_UserNotFound() {
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setEmailId("missing@example.com");
        loginDTO.setPassword("plain123");

        when(userRepository.findUserByEmail("missing@example.com")).thenReturn(null);

        assertThrows(UserIdNotFoundException.class,
                () -> service.userLogin(loginDTO));
    }

    // Negative: userLogin invalid password
    @Test
    void testUserLogin_InvalidPassword() {
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setEmailId("test@example.com");
        loginDTO.setPassword("wrongpass");

        User user = new User();
        user.setUserId(1);
        user.setEmailId("test@example.com");
        user.setPassword("encoded123");

        when(userRepository.findUserByEmail("test@example.com")).thenReturn(user);
        when(passwordEncoder.matches("wrongpass", "encoded123")).thenReturn(false);

        assertThrows(RuntimeException.class,
                () -> service.userLogin(loginDTO));
    }
}
