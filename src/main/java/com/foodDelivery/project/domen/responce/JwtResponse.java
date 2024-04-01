package com.foodDelivery.project.domen.responce;

import com.foodDelivery.project.domen.model.enums.UserRole;

public class JwtResponse {
    private String token;

    private String username;

    private UserRole role;

    public JwtResponse(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
