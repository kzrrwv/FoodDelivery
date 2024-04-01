package com.foodDelivery.project;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class Encoder {
    public static void main(String[] args) {
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

        String password = "password123";

        System.out.println(bCryptPasswordEncoder.encode(password));
    }
}
