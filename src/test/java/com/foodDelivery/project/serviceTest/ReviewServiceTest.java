package com.foodDelivery.project.serviceTest;

import com.foodDelivery.project.domen.dto.ReviewDTO;
import com.foodDelivery.project.domen.model.Order;
import com.foodDelivery.project.domen.model.Review;
import com.foodDelivery.project.domen.model.User;
import com.foodDelivery.project.exception.BusinessException;
import com.foodDelivery.project.repository.ReviewRepository;
import com.foodDelivery.project.repository.UserRepository;
import com.foodDelivery.project.service.impl.ReviewServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ReviewServiceImpl reviewService;

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContext securityContext;

    private User testUser;
    private Order testOrder;
    private Review testReview;
    private ReviewDTO testReviewDTO;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testUser");

        testOrder = new Order();
        testOrder.setId(1L);
        testOrder.setUser_id(testUser);

        testReview = new Review();
        testReview.setId(1L);
        testReview.setRating(5);
        testReview.setComment("Great!");
        testReview.setUser_id(testUser);
        testReview.setOrder_id(testOrder);

        testReviewDTO = new ReviewDTO();
        testReviewDTO.setRating(4);
        testReviewDTO.setComment("Good but not perfect");

        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getName()).thenReturn("testUser");
    }

    @Test
    void shouldCreateReviewWithOrder_whenValid() {
        // Arrange
        when(userRepository.findUserByUsername("testUser")).thenReturn(Optional.of(testUser));
        when(reviewRepository.save(any(Review.class))).thenAnswer(inv -> inv.getArgument(0));

        // Act
        Review result = reviewService.createReviewWithOrder(testReviewDTO, testOrder);

        // Assert
        assertThat(result.getRating()).isEqualTo(4);
        assertThat(result.getComment()).isEqualTo("Good but not perfect");
        assertThat(result.getOrder_id()).isEqualTo(testOrder);
        assertThat(result.getUser_id()).isEqualTo(testUser);
        verify(reviewRepository, times(1)).save(any(Review.class));
    }

    @Test
    void shouldThrowException_whenReviewNotFoundOnUpdate() {
        // Arrange
        when(reviewRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            reviewService.updateReview(999L, testReviewDTO);
        });

        assertThat(exception.getMessage()).contains("Отзыв не найден");
        assertThat(exception.getHttpStatus()).isEqualTo(HttpStatus.NOT_FOUND);
        verify(reviewRepository, never()).save(any(Review.class));
    }

    @Test
    void shouldThrowException_whenUserDoesNotOwnReview() {
        // Arrange
        User differentUser = new User();
        differentUser.setId(2L);
        differentUser.setUsername("differentUser");

        Review ownedByDifferentUser = new Review();
        ownedByDifferentUser.setId(1L);
        ownedByDifferentUser.setUser_id(differentUser);

        when(reviewRepository.findById(1L)).thenReturn(Optional.of(ownedByDifferentUser));
        when(userRepository.findUserByUsername("testUser")).thenReturn(Optional.of(testUser));

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            reviewService.updateReview(1L, testReviewDTO);
        });

        assertThat(exception.getMessage()).contains("Это не ваш отзыв");
        assertThat(exception.getHttpStatus()).isEqualTo(HttpStatus.FORBIDDEN);
    }
}