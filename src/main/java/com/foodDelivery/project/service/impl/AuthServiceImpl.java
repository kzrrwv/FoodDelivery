package com.foodDelivery.project.service.impl;

import com.foodDelivery.project.domen.dto.LoginRequest;
import com.foodDelivery.project.domen.dto.RegisterUserRequest;
import com.foodDelivery.project.security.JwtService;
import com.foodDelivery.project.service.AuthService;
import com.foodDelivery.project.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {
    private UserService service;
    private JwtService jwtService;

    @Override
    public String signUp(RegisterUserRequest registerUserRequest) {
        return "";
    }

    @Override
    public String signIn(LoginRequest loginRequest) {
        return "";
    }
}
