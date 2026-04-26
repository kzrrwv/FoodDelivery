package com.foodDelivery.project.security;

import com.foodDelivery.project.domen.request.LoginRequest;
import com.foodDelivery.project.domen.request.RegisterUserRequest;
import com.foodDelivery.project.domen.model.User;
import com.foodDelivery.project.domen.model.enums.UserRole;
import com.foodDelivery.project.domen.responce.JwtResponse;
import com.foodDelivery.project.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AuthService {
    private final PasswordEncoder passwordEncoder;
    private UserRepository repository;
    private JwtService jwtService;
    private AuthenticationManager authenticationManager;

    public AuthService(PasswordEncoder passwordEncoder, UserRepository repository, JwtService jwtService, AuthenticationManager authenticationManager) {
        this.passwordEncoder = passwordEncoder;
        this.repository = repository;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    public JwtResponse login(LoginRequest request){
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        Optional<User> userByUsername = repository.findUserByUsername(request.getUsername());

        if(userByUsername.isEmpty()){
            throw new RuntimeException();
        }
        User user = userByUsername.get();
        String token = jwtService.generateToken(new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                List.of(new SimpleGrantedAuthority(user.getRole().name()))
        ));

        return new JwtResponse(token);
    }

    public JwtResponse register(RegisterUserRequest request){
        User user = new User();

        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEmail(request.getEmail());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setUsername(request.getUsername());
        user.setRole(UserRole.ROLE_USER);

        repository.save(user);
        String token = jwtService.generateToken(new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                List.of(new SimpleGrantedAuthority(user.getRole().name())))
        );

        return new JwtResponse(token);
    }
}
