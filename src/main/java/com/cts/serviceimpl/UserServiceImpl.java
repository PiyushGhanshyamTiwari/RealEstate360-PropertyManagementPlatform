package com.cts.serviceimpl;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.cts.annotation.Audit;
import com.cts.constants.AuditActions;
import com.cts.dto.AuditLogRequestDTO;
import com.cts.dto.LoginDTO;
import com.cts.dto.LoginResponseDTO;
import com.cts.dto.RegistrationInputDTO;
import com.cts.dto.RegistrationOutputDTO;
import com.cts.entity.User;
import com.cts.mapper.UserRegistrationMapper;
import com.cts.repository.UserRepository;
import com.cts.service.AuditLogService;
import com.cts.service.UserService;
import com.cts.config.JWTUtil;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JWTUtil jwtUtil;
    private final UserRegistrationMapper mapper;
    private final AuditLogService auditLogService;

    @Override
    @Audit(action = AuditActions.REGISTER_USER, resourceType = "User")
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
    @Audit(action = AuditActions.LOGIN_USER, resourceType = "User")
    public LoginResponseDTO userLogin(LoginDTO loginDTO) {

        User user = userRepository.findUserByEmail(loginDTO.getEmailId());

        // User not found → log FAILED and return null
        if (user == null) {
            auditLogService.logAction(AuditLogRequestDTO.builder()
                    .userId(null)
                    .action(AuditActions.LOGIN_USER)
                    .resourceType("User")
                    .resourceId(null)
                    .status("FAILED")
                    .details("LOGIN_USER FAILED | Reason: No account found for email: "
                            + loginDTO.getEmailId())
                    .build());
            return null;
        }

        // Wrong password → log FAILED
        if (!passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())) {
            auditLogService.logAction(AuditLogRequestDTO.builder()
                    .userId(user.getUserId())
                    .action(AuditActions.LOGIN_USER)
                    .resourceType("User")
                    .resourceId(String.valueOf(user.getUserId()))
                    .status("FAILED")
                    .details("LOGIN_USER FAILED | Reason: Invalid password for email: "
                            + loginDTO.getEmailId())
                    .build());
            return null;
        }

        // Success → log SUCCESS with userId and resourceId both populated
        String token = jwtUtil.generateToken(loginDTO.getEmailId());

        auditLogService.logAction(AuditLogRequestDTO.builder()
                .userId(user.getUserId())
                .action(AuditActions.LOGIN_USER)
                .resourceType("User")
                .resourceId(String.valueOf(user.getUserId()))
                .status("SUCCESS")
                .details("LOGIN_USER performed on User [id=" + user.getUserId() + "]")
                .build());

        return new LoginResponseDTO(user.getUserId(), user.getUserName(),
                user.getEmailId(), user.getPhone(), user.getRole(), token);
    }
}
