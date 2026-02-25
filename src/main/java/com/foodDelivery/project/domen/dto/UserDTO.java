package com.foodDelivery.project.domen.dto;

import com.foodDelivery.project.domen.model.enums.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class UserDTO {

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
}
