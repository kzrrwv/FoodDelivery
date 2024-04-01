package com.foodDelivery.project.serviceTest;

import com.foodDelivery.project.domen.dto.ReviewDTO;
import com.foodDelivery.project.domen.model.Order;
import com.foodDelivery.project.domen.model.Review;
import com.foodDelivery.project.domen.responce.ReviewToRetrieve;
import com.foodDelivery.project.exception.BusinessException;
import com.foodDelivery.project.repository.ReviewRepository;
import com.foodDelivery.project.service.impl.ReviewServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    @Mock
    private ReviewRepository repository;

    @InjectMocks
    private ReviewServiceImpl service;

    @Test
    void createReview_success() {

        ReviewDTO dto = new ReviewDTO();
        dto.setComment("good");
        dto.setRating(5);

        service.createReview(dto);

        verify(repository).save(any(Review.class));
    }

    @Test
    void getReviewById_success() {

        Review review = new Review();

        when(repository.findById(1L))
                .thenReturn(Optional.of(review));

        ReviewToRetrieve result = service.getReviewById(1L);

        assertNotNull(result);
    }

    @Test
    void findReviewsWithPageble_success() {

        Review review = new Review();

        when(repository.findAll(any(PageRequest.class)))
                .thenReturn(new PageImpl<>(List.of(review)));

        List<ReviewToRetrieve> result =
                service.findReviewsWithPageble(PageRequest.of(0, 5));

        assertEquals(1, result.size());
    }

    @Test
    void updateReview_success() {

        Review review = new Review();

        when(repository.findById(1L))
                .thenReturn(Optional.of(review));

        when(repository.save(any(Review.class)))
                .thenReturn(review);

        ReviewDTO dto = new ReviewDTO();
        dto.setComment("updated");

        ReviewDTO result = service.updateReview(1L, dto);

        assertNotNull(result);
    }

    @Test
    void delete_notFound() {

        when(repository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(BusinessException.class,
                () -> service.delete(1L));
    }
}