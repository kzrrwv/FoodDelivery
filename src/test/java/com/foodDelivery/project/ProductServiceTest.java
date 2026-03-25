package com.foodDelivery.project;

import com.foodDelivery.project.repository.ProductRepository;
import com.foodDelivery.project.service.impl.ProductServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class ProductServiceTest {
    @Mock
    private ProductRepository repository;

    @InjectMocks
    private ProductServiceImpl service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createProduct_shouldSaveProduct(){

    }

    @Test
    void getProducts_shouldReturnList(){

    }

    @Test
    void deleteProduct_shouldDeleteProduct(){

    }

    @Test
    void deleteProduct_shouldThrowException(){

    }
}
