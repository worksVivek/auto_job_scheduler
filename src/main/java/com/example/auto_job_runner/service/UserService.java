package com.example.auto_job_runner.service;

import org.springframework.stereotype.Service;
import com.example.auto_job_runner.entity.User;
import com.example.auto_job_runner.enums.Role;
import com.example.auto_job_runner.dto.RegisterRequest;
import com.example.auto_job_runner.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public void register(RegisterRequest request) {
        if (userRepository.findByUsername(request.username()).isPresent()) {
            throw new RuntimeException("Username already exists");
        }
        User user = new User();
        user.setUsername(request.username());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setRole(Role.ROLE_ADMIN);
        userRepository.save(user);
    }
}
