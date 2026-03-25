package com.foodDelivery.project;

import com.foodDelivery.project.repository.UserRepository;
import com.foodDelivery.project.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class UserServiceTest {

    @Mock
    private UserRepository repository;

    @InjectMocks
    private UserServiceImpl service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createUser_shouldSaveUser(){

    }

    @Test
    void getUsers_shouldReturnList(){

    }

    @Test
    void deleteUser_shouldDeleteUser(){

    }

    @Test
    void deleteUser_shouldThrowException(){

    }
}
