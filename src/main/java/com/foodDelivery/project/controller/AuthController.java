package com.foodDelivery.project.controller;

import com.foodDelivery.project.domen.dto.LoginRequest;
import com.foodDelivery.project.domen.dto.RegisterUserRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @PostMapping("/register")
    public String register(@RequestBody RegisterUserRequest registerUserResponse){
        return "register";
    }

    @PostMapping("/login")
    public String login(@RequestBody LoginRequest loginRequestDTO){
        return "login";
    }
}
