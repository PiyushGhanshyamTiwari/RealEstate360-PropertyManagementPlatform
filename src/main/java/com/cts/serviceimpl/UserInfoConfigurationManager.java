package com.cts.serviceimpl;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.cts.entity.User;
import com.cts.repository.UserRepository;

import lombok.AllArgsConstructor;
import lombok.Data;

@Service
@Data
@AllArgsConstructor
public class UserInfoConfigurationManager implements UserDetailsService {
    private UserRepository userRepository;

	@Override
    public UserDetails loadUserByUsername(String emailId) throws UsernameNotFoundException {
        User user=userRepository.findUserByEmail(emailId);
        if (user != null) {
        	String [] roles = {user.getRole().toUpperCase()};
            return org.springframework.security.core.userdetails.User.builder()
                    .username(user.getEmailId())
                    .password(user.getPassword())
                    .roles(roles)
                    //.authorities(user.getRoles().toArray(new String[0])) // expects ROLE_USER, ROLE_ADMIN
                    .build();
        }
        throw new UsernameNotFoundException("User not found with emailId: " + emailId);
    }
}
