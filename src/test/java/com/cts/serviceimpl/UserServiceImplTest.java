package com.cts.serviceimpl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;

import com.cts.dto.*;
import com.cts.entity.User;
import com.cts.mapper.UserRegistrationMapper;
import com.cts.repository.UserRepository;
import com.cts.config.JWTUtil;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private JWTUtil jwtUtil;
    @Mock private UserRegistrationMapper mapper;

    @InjectMocks
    private UserServiceImpl service;

    private User user;

    @BeforeEach
    void setup() {
        user = new User();
        user.setUserId(1);
        user.setUserName("Piyush");
        user.setEmailId("test@mail.com");
        user.setPassword("encodedPwd");
        user.setPhone(Long.parseLong("999999999"));
        user.setRole("USER");
    }

    
    @Test
    void testRegisterUserSuccess() {
        RegistrationInputDTO input = new RegistrationInputDTO();
        input.setEmailId("test@mail.com");

        when(userRepository.existsByEmailId("test@mail.com")).thenReturn(false);
        when(mapper.convertToUser(input)).thenReturn(user);
        when(passwordEncoder.encode(any())).thenReturn("encodedPwd");
        when(userRepository.save(user)).thenReturn(user);
        when(mapper.convertToUserResponseDTO(user))
                .thenReturn(new RegistrationOutputDTO());

        RegistrationOutputDTO result = service.registerUser(input);

        assertNotNull(result);
        verify(userRepository).save(user);
        verify(passwordEncoder).encode(any());
    }

   
    @Test
    void testRegisterUserEmailExists() {
        RegistrationInputDTO input = new RegistrationInputDTO();
        input.setEmailId("test@mail.com");

        when(userRepository.existsByEmailId("test@mail.com")).thenReturn(true);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> service.registerUser(input));

        assertEquals("Email already exists", ex.getMessage());
    }

    
    @Test
    void testGetAllUsers() {
        when(userRepository.findAll()).thenReturn(List.of(user));

        List<User> result = service.getAllUsers();

        assertEquals(1, result.size());
    }

  
    @Test
    void testUserLoginSuccess() {
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setEmailId("test@mail.com");
        loginDTO.setPassword("pwd");

        when(userRepository.findUserByEmail("test@mail.com")).thenReturn(user);
        when(passwordEncoder.matches("pwd", "encodedPwd")).thenReturn(true);
        when(jwtUtil.generateToken("test@mail.com")).thenReturn("token123");

        LoginResponseDTO result = service.userLogin(loginDTO);

        assertNotNull(result);
        assertEquals("token123", result.getToken());
    }

    
    @Test
    void testUserLoginWrongPassword() {
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setEmailId("test@mail.com");
        loginDTO.setPassword("wrong");

        when(userRepository.findUserByEmail("test@mail.com")).thenReturn(user);
        when(passwordEncoder.matches("wrong", "encodedPwd")).thenReturn(false);

        LoginResponseDTO result = service.userLogin(loginDTO);

        assertNull(result);
    }

    
    @Test
    void testUserLoginUserNotFound() {
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setEmailId("test@mail.com");

        when(userRepository.findUserByEmail("test@mail.com")).thenReturn(null);

        LoginResponseDTO result = service.userLogin(loginDTO);

        assertNull(result);
    }
}