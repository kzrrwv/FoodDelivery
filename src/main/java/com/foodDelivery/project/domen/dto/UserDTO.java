package com.foodDelivery.project.domen.dto;

import com.foodDelivery.project.domen.model.User;
import com.foodDelivery.project.domen.model.enums.UserRole;
import com.foodDelivery.project.service.impl.UserServiceImpl;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserDTO {

    private static final Logger log = LoggerFactory.getLogger(UserDTO.class);

    @NotBlank
    @Size(min = 5, max = 15, message = "Username должен быть от 5 до 15 символов!")
    private String username;

    @Email
    @Pattern(regexp = "^[a-zA-Z][A-Za-z0-9._-]+@[A-Za-z0-9]+\\.[A-Za-z]{2,6}\\b")
    private String email;

    @Pattern(regexp = "^\\+7\\s?\\(?[0-9]{3}\\)?\\s?[0-9]{3}[\\-\\s]?[0-9]{2}[\\-\\s]?[0-9]{2}$")
    private String phoneNumber;

    @Size(min = 8, max = 20)
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).+$", message = "Пароль должен содержать одну цифру, одну строчную и одну заглавную букву!")
    private String password;

    @NotBlank
    private String role;

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getPassword() {
        return password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
