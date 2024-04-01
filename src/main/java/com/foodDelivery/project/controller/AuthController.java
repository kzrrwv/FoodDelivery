package com.foodDelivery.project.controller;

import com.foodDelivery.project.domen.request.LoginRequest;
import com.foodDelivery.project.domen.request.RegisterUserRequest;
import com.foodDelivery.project.domen.responce.JwtResponse;
import com.foodDelivery.project.security.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@Tag(name = "Auth", description = "Регистрация и авторизация")
public class AuthController {

    private AuthService service;

    public AuthController(AuthService service) {
        this.service = service;
    }

    @PostMapping("/register")
    @Operation(summary = "Регистрация пользователя")
    @ApiResponse(responseCode = "200", description = "Пользователь зарегистрирован")
    public JwtResponse register(@RequestBody RegisterUserRequest registerUserResponse){
        return service.register(registerUserResponse);
    }

    @PostMapping("/login")
    @Operation(summary = "Вход в систему")
    @ApiResponse(responseCode = "200", description = "JWT токен выдан")
    public JwtResponse login(@RequestBody LoginRequest loginRequestDTO){
        return service.login(loginRequestDTO);
    }
}
