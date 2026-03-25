package com.foodDelivery.project.service;

import com.foodDelivery.project.domen.dto.LoginRequest;
import com.foodDelivery.project.domen.dto.RegisterUserRequest;

public interface AuthService {
    String signUp(RegisterUserRequest registerUserRequest);
    String signIn(LoginRequest loginRequest);
}
