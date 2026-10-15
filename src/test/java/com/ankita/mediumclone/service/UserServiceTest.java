package com.ankita.mediumclone.service;

import com.ankita.mediumclone.dto.UserResponse;
import com.ankita.mediumclone.entity.User;
import com.ankita.mediumclone.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.ankita.mediumclone.exception.UserAlreadyExistsException;
import com.ankita.mediumclone.dto.LoginRequest;
import com.ankita.mediumclone.dto.LoginResponse;
import com.ankita.mediumclone.exception.InvalidCredentialsException;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserService(
                userRepository,
                passwordEncoder,
                jwtService
        );
    }

    @Test
    void registerUser_shouldRegisterNewUser() {

        User user = new User();
        user.setUsername("Ankita");
        user.setEmail("ankita@gmail.com");
        user.setPassword("abc123");

        when(userRepository.existsByEmail("ankita@gmail.com"))
                .thenReturn(false);

        when(passwordEncoder.encode("abc123"))
                .thenReturn("hashedPassword");

        when(userRepository.save(user))
                .thenReturn(user);

        UserResponse response = userService.registerUser(user);

        assertEquals("Ankita", response.getUsername());
        assertEquals("ankita@gmail.com", response.getEmail());

        verify(userRepository).existsByEmail("ankita@gmail.com");
        verify(passwordEncoder).encode("abc123");
        verify(userRepository).save(user);
    }
    @Test
    void registerUser_shouldThrowExceptionWhenEmailAlreadyExists() {

        User user = new User();
        user.setUsername("Ankita");
        user.setEmail("ankita@gmail.com");
        user.setPassword("abc123");

        when(userRepository.existsByEmail("ankita@gmail.com"))
                .thenReturn(true);

        assertThrows(
                UserAlreadyExistsException.class,
                () -> userService.registerUser(user)
        );

        verify(userRepository).existsByEmail("ankita@gmail.com");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void loginUser_shouldLoginWithValidCredentials() {

        User user = mock(User.class);

        when(user.getId()).thenReturn(1L);
        when(user.getUsername()).thenReturn("Ankita");
        when(user.getEmail()).thenReturn("ankita@gmail.com");
        when(user.getPassword()).thenReturn("hashedPassword");

        LoginRequest loginRequest = new LoginRequest(
                "ankita@gmail.com",
                "abc123"
        );

        when(userRepository.findByEmail("ankita@gmail.com"))
                .thenReturn(java.util.Optional.of(user));

        when(passwordEncoder.matches("abc123", "hashedPassword"))
                .thenReturn(true);

        when(jwtService.generateToken("ankita@gmail.com"))
                .thenReturn("test-token");

        LoginResponse response = userService.loginUser(loginRequest);

        assertEquals("test-token", response.getToken());
        assertEquals(1L, response.getId());
        assertEquals("Ankita", response.getUsername());
        assertEquals("ankita@gmail.com", response.getEmail());

        verify(userRepository).findByEmail("ankita@gmail.com");
        verify(passwordEncoder).matches("abc123", "hashedPassword");
        verify(jwtService).generateToken("ankita@gmail.com");
    }

    @Test
    void loginUser_shouldThrowExceptionWhenPasswordIsWrong() {

        User user = mock(User.class);

        when(user.getPassword()).thenReturn("hashedPassword");

        LoginRequest loginRequest = new LoginRequest(
                "ankita@gmail.com",
                "wrongPassword"
        );

        when(userRepository.findByEmail("ankita@gmail.com"))
                .thenReturn(java.util.Optional.of(user));

        when(passwordEncoder.matches("wrongPassword", "hashedPassword"))
                .thenReturn(false);

        assertThrows(
                InvalidCredentialsException.class,
                () -> userService.loginUser(loginRequest)
        );

        verify(userRepository).findByEmail("ankita@gmail.com");
        verify(passwordEncoder).matches("wrongPassword", "hashedPassword");
        verify(jwtService, never()).generateToken(anyString());
    }

}