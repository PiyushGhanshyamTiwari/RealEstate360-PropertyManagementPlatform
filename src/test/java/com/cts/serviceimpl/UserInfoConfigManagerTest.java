package com.cts.serviceimpl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.cts.entity.User;
import com.cts.repository.UserRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@ExtendWith(MockitoExtension.class)
class UserInfoConfigurationManagerTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserInfoConfigurationManager service;

    @Test
    void testLoadUserByUsernameSuccess() {
        User user = new User();
        user.setEmailId("test@example.com");
        user.setPassword("password123");
        user.setRole("admin");

        when(userRepository.findUserByEmail("test@example.com")).thenReturn(user);

        UserDetails result = service.loadUserByUsername("test@example.com");

        assertNotNull(result);
        assertEquals("test@example.com", result.getUsername());
        assertEquals("password123", result.getPassword());
        assertTrue(result.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN")));
    }

    @Test
    void testLoadUserByUsernameNotFound() {
        when(userRepository.findUserByEmail("missing@example.com")).thenReturn(null);

        assertThrows(UsernameNotFoundException.class,
                () -> service.loadUserByUsername("missing@example.com"));
    }
}
