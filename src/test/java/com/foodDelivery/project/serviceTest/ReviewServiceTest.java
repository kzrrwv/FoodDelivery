package com.foodDelivery.project.serviceTest;

import com.foodDelivery.project.domen.dto.ReviewDTO;
import com.foodDelivery.project.domen.model.Review;
import com.foodDelivery.project.domen.responce.ReviewToRetrieve;
import com.foodDelivery.project.repository.ReviewRepository;
import com.foodDelivery.project.service.impl.ReviewServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ReviewServiceTest {

    @Mock
    private ReviewRepository repository;

    @InjectMocks
    private ReviewServiceImpl service;

    @Test
    void createReview_shouldSave() {
        ReviewDTO dto = new ReviewDTO();
        dto.setRating(5);

        service.createReview(dto);

        verify(repository).save(any(Review.class));
    }

    @Test
    void getReviews_shouldReturnMappedList() {
        Review review = new Review();
        review.setRating(4);
        review.setComment("good");

        when(repository.findAll()).thenReturn(List.of(review));

        List<ReviewToRetrieve> result = service.getReviews();

        assertEquals(1, result.size());
        assertEquals(4, result.get(0).getRating());
    }

    @Test
    void pageable_shouldReturnNull_currentBug() {
        Page<Review> page = new PageImpl<>(List.of(new Review()));

        when(repository.findAll(any(PageRequest.class)))
                .thenReturn(page);

        List<ReviewToRetrieve> result =
                service.findReviewsWithPageble(PageRequest.of(0, 10));

        assertNull(result); // баг в сервисе
    }

    @Test
    void update_shouldChangeRating() {
        Review review = new Review();
        review.setRating(1);

        when(repository.findById(1L)).thenReturn(Optional.of(review));
        when(repository.save(any())).thenReturn(review);

        ReviewDTO dto = new ReviewDTO();
        dto.setRating(10);

        ReviewDTO result = service.updateReview(1L, dto);

        assertEquals(10, result.getRating());
    }

    @Test
    void delete_shouldCallRepository() {
        Review review = new Review();

        when(repository.findById(1L)).thenReturn(Optional.of(review));

        service.delete(1L);

        verify(repository).delete(review);
    }
}
