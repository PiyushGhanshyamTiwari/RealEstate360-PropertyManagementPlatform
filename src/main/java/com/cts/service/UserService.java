package com.cts.service;

import java.util.List;

import com.cts.dto.LoginDTO;
import com.cts.dto.LoginResponseDTO;
import com.cts.dto.RegistrationInputDTO;
import com.cts.dto.RegistrationOutputDTO;
import com.cts.entity.User;

public interface UserService {
	public RegistrationOutputDTO registerUser(RegistrationInputDTO dto);
	public List<User> getAllUsers();
	public LoginResponseDTO userLogin(LoginDTO loginDTO);
    public RegistrationOutputDTO updateUser(Integer userId,RegistrationInputDTO registerInputDTO);
}
