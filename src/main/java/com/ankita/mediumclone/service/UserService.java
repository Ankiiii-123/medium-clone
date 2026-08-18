package com.ankita.mediumclone.service;
import com.ankita.mediumclone.dto.UserResponse;

import com.ankita.mediumclone.entity.User;
import com.ankita.mediumclone.exception.UserAlreadyExistsException;
import com.ankita.mediumclone.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;


@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserResponse registerUser(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new UserAlreadyExistsException("User already exists");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User savedUser = userRepository.save(user);
        return new UserResponse ( savedUser.getId(), savedUser.getUsername(), savedUser.getEmail() );

    }
}