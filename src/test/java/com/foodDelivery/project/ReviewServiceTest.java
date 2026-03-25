package com.foodDelivery.project;

import com.foodDelivery.project.repository.ReviewRepository;
import com.foodDelivery.project.service.impl.ReviewServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class ReviewServiceTest {

    @Mock
    private ReviewRepository repository;

    @InjectMocks
    private ReviewServiceImpl service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createReview_shouldSaveReview(){

    }

    @Test
    void getReviews_shouldReturnList(){

    }

    @Test
    void deleteReview_shouldDeleteReview(){

    }

    @Test
    void deleteReview_shouldThrowException(){

    }
}
