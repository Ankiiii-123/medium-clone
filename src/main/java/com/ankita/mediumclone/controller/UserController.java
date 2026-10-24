package com.ankita.mediumclone.controller;

import com.ankita.mediumclone.service.UserService;
import com.ankita.mediumclone.entity.User;
import com.ankita.mediumclone.dto.UserResponse;
import com.ankita.mediumclone.dto.LoginRequest;
import org.springframework.web.bind.annotation.*;
import com.ankita.mediumclone.dto.LoginResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/users")
public class UserController {


    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/test")
    public String test() {
        return "User API is working!";
    }

    @GetMapping("/profile")
    public String profile() {
        return "You are authenticated!";
    }

    @PostMapping("/register")
    public UserResponse register(@RequestBody User user) {
        return userService.registerUser(user);
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest loginRequest) {
        return userService.loginUser(loginRequest);
    }


}