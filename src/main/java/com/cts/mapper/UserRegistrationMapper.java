package com.cts.mapper;

import org.springframework.stereotype.Component;

import com.cts.dto.RegistrationInputDTO;
import com.cts.dto.RegistrationOutputDTO;
import com.cts.entity.User;

import java.time.LocalDate;

@Component
public class UserRegistrationMapper {

    // Convert Input DTO → Entity
    public User convertToUser(RegistrationInputDTO dto) {
        User user = new User();
        user.setUserName(dto.getUserName());
        user.setEmailId(dto.getEmailId());
        user.setPhone(Long.parseLong(dto.getPhone())); 
        user.setRole(dto.getRole());
        user.setPassword(dto.getPassword());
        user.setRegisteredOn(LocalDate.now());
        return user;
    }

    // Convert Entity → Output DTO
    public RegistrationOutputDTO convertToUserResponseDTO(User user) {
        RegistrationOutputDTO response = new RegistrationOutputDTO ();
        response.setUserId(user.getUserId());
        response.setUserName(user.getUserName());
        response.setEmailId(user.getEmailId());
        response.setPhone(user.getPhone());
        response.setRole(user.getRole());
        response.setRegisteredOn(user.getRegisteredOn());
        return response;
    }
}