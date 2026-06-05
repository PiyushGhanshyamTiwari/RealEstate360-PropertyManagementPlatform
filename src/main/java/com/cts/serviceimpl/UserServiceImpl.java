package com.cts.serviceimpl;

import java.time.LocalDate;
import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.cts.dto.LoginDTO;
import com.cts.dto.LoginResponseDTO;
import com.cts.entity.User;
import com.cts.repository.UserRepository;
import com.cts.service.UserService;
import com.cts.util.JWTUtil;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final JWTUtil jwtUtil;

	@Override
	public User addUser(User user) {
		user.setRegisteredOn(LocalDate.now());
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		return userRepository.save(user);
	}

	@Override
	public List<User> getAllUsers() {
		return userRepository.findAll();
	}

	@Override
	public LoginResponseDTO userLogin(LoginDTO loginDTO) {
		User user = userRepository.getUserByEmail(loginDTO.getEmailId());
		if(user==null)
			return null;
		String existingPwd = user.getPassword();
		if(passwordEncoder.matches(loginDTO.getPassword(), existingPwd)) {
			String token = jwtUtil.generateToken(loginDTO.getEmailId());
			LoginResponseDTO response = new LoginResponseDTO(user.getUserId(), user.getUserName(),user.getEmailId(), user.getPhone(), user.getRole(),token);
			return response;
		}
		else {
			return null;
		}
	} 
	

}
