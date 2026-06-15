//package com.cts.serviceimpl;
//
//import com.cts.entity.User;
//import com.cts.repository.UserRepository;
//import com.cts.serviceimpl.UserInfoConfigManager;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//public class UserInfoConfigManagerTest {
//
//    @Mock
//    private UserRepository userRepository;
//
//    @InjectMocks
//    private UserInfoConfigManager service;
//
//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);
//    }
//
//    // Positive: loadUserByUsername succeeds
//    @Test
//    void testLoadUserByUsername_Success() {
//        User user = new User();
//        user.setEmailId("test@example.com");
//        user.setPassword("password123");
//        user.setRole("ADMIN");
//
//        when(userRepository.findUserByEmail("test@example.com")).thenReturn(user);
//
//        UserDetails result = service.loadUserByUsername("test@example.com");
//
//        assertNotNull(result);
//        assertEquals("test@example.com", result.getUsername());
//        assertEquals("password123", result.getPassword());
//        assertTrue(result.getAuthorities().stream()
//                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN")));
//    }
//
//    // Negative: user not found
//    @Test
//    void testLoadUserByUsername_NotFound() {
//        when(userRepository.findUserByEmail("missing@example.com")).thenReturn(null);
//
//        assertThrows(UsernameNotFoundException.class,
//                () -> service.loadUserByUsername("missing@example.com"));
//    }
//}
