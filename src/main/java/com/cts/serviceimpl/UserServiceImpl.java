package com.cts.serviceimpl;

import java.time.LocalDate;
import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.cts.dto.LoginDTO;
import com.cts.dto.LoginResponseDTO;
import com.cts.dto.RegistrationInputDTO;
import com.cts.dto.RegistrationOutputDTO;
import com.cts.entity.User;
import com.cts.mapper.UserRegistrationMapper;
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
	private final UserRegistrationMapper mapper;

	@Override
    public RegistrationOutputDTO registerUser(RegistrationInputDTO dto) {

        if (userRepository.existsByEmailId(dto.getEmailId())) {
            throw new RuntimeException("Email already exists");
        }
        User user = mapper.convertToUser(dto);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User savedUser = userRepository.save(user);
        return mapper.convertToUserResponseDTO(savedUser);
    }
	
	@Override
	public List<User> getAllUsers() {
		return userRepository.findAll();
	}

	@Override
	public LoginResponseDTO userLogin(LoginDTO loginDTO) {
		User user = userRepository.findUserByEmail(loginDTO.getEmailId());
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
