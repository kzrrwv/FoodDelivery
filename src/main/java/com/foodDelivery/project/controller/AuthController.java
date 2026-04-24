package com.foodDelivery.project.controller;

import com.foodDelivery.project.domen.dto.LoginRequest;
import com.foodDelivery.project.domen.dto.RegisterUserRequest;
import com.foodDelivery.project.domen.responce.JwtResponse;
import com.foodDelivery.project.security.AuthService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private AuthService service;

    public AuthController(AuthService service) {
        this.service = service;
    }

    @PostMapping("/register")
    public JwtResponse register(@RequestBody RegisterUserRequest registerUserResponse){
        return service.register(registerUserResponse);
    }

    @PostMapping("/login")
    public JwtResponse login(@RequestBody LoginRequest loginRequestDTO){
        return service.login(loginRequestDTO);
    }

    @GetMapping("/get")
    public String get(){
        return "Достигли";
    }
}
