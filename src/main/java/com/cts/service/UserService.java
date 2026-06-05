package com.cts.service;

import java.util.List;

import com.cts.dto.LoginDTO;
import com.cts.dto.LoginResponseDTO;
import com.cts.entity.User;

public interface UserService {
	public User addUser(User user);
	public List<User> getAllUsers();
	public LoginResponseDTO userLogin(LoginDTO loginDTO);

}
