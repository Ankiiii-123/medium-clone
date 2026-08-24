package com.ankita.mediumclone.controller;

import com.ankita.mediumclone.service.UserService;
import com.ankita.mediumclone.entity.User;
import com.ankita.mediumclone.dto.UserResponse;
import com.ankita.mediumclone.dto.LoginRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.ankita.mediumclone.dto.LoginResponse;

@RestController
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/users/test")
    public String test() {
        return "User API is working!";
    }

    @PostMapping("/users/register")
    public UserResponse register(@RequestBody User user) {
        return userService.registerUser(user);
    }

    @PostMapping("/users/login")
    public LoginResponse login(@RequestBody LoginRequest loginRequest) {
        return userService.loginUser(loginRequest);
    }

}