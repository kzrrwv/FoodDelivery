package com.foodDelivery.project.serviceTest;

import com.foodDelivery.project.domen.dto.ReviewDTO;
import com.foodDelivery.project.domen.model.Order;
import com.foodDelivery.project.domen.model.Review;
import com.foodDelivery.project.domen.model.User;
import com.foodDelivery.project.domen.model.enums.UserRole;
import com.foodDelivery.project.domen.responce.ReviewToRetrieve;
import com.foodDelivery.project.exception.BusinessException;
import com.foodDelivery.project.repository.ReviewRepository;
import com.foodDelivery.project.repository.UserRepository;
import com.foodDelivery.project.service.impl.ReviewServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContext securityContext;

    @InjectMocks
    private ReviewServiceImpl reviewService;

    private User testUser;
    private Order testOrder;
    private Review testReview;
    private ReviewDTO testReviewDTO;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("john_doe");
        testUser.setRole(UserRole.ROLE_USER);

        testOrder = new Order();
        testOrder.setId(100L);
        testOrder.setUser_id(testUser);

        testReview = new Review();
        testReview.setId(10L);
        testReview.setRating(5);
        testReview.setComment("Great food!");
        testReview.setUser_id(testUser);
        testReview.setOrder_id(testOrder);
        testReview.setCreatedAt(LocalDateTime.now());

        testReviewDTO = new ReviewDTO();
        testReviewDTO.setRating(5);
        testReviewDTO.setComment("Great food!");
        testReviewDTO.setCreatedAt(LocalDateTime.now());

        SecurityContextHolder.setContext(securityContext);
    }

    //позитивные сценарии

    @Test
    void createReviewWithOrder_Success_ShouldCreateReview() {
        when(reviewRepository.save(any(Review.class))).thenReturn(testReview);

        Review result = reviewService.createReviewWithOrder(testReviewDTO, testOrder);

        assertNotNull(result);
        assertEquals(5, result.getRating());
        assertEquals("Great food!", result.getComment());
        assertEquals(testOrder, result.getOrder_id());
        assertEquals(testUser, result.getUser_id());
        verify(reviewRepository).save(any(Review.class));
    }

    @Test
    void getReviews_Success_ShouldReturnList() {
        when(reviewRepository.findAll()).thenReturn(List.of(testReview));

        List<ReviewToRetrieve> result = reviewService.getReviews();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Great food!", result.get(0).getComment());
        verify(reviewRepository).findAll();
    }

    @Test
    void getReviewById_Success_ShouldReturnReview() {
        when(reviewRepository.findById(10L)).thenReturn(Optional.of(testReview));

        ReviewToRetrieve result = reviewService.getReviewById(10L);

        assertNotNull(result);
        assertEquals("Great food!", result.getComment());
        assertEquals(5, result.getRating());
        verify(reviewRepository).findById(10L);
    }

    @Test
    void findReviewsWithPageble_Success_ShouldReturnPagedReviews() {
        PageRequest pageRequest = PageRequest.of(0, 10);
        Page<Review> reviewPage = new PageImpl<>(List.of(testReview));
        when(reviewRepository.findAll(pageRequest)).thenReturn(reviewPage);

        List<ReviewToRetrieve> result = reviewService.findReviewsWithPageble(pageRequest);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(reviewRepository).findAll(pageRequest);
    }

    @Test
    void updateReview_Success_ShouldUpdateAndReturnDTO() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("john_doe");
        when(userRepository.findUserByUsername("john_doe")).thenReturn(Optional.of(testUser));
        when(reviewRepository.findById(10L)).thenReturn(Optional.of(testReview));
        when(reviewRepository.save(any(Review.class))).thenReturn(testReview);

        ReviewDTO result = reviewService.updateReview(10L, testReviewDTO);

        assertNotNull(result);
        assertEquals("Great food!", result.getComment());
        verify(reviewRepository).save(testReview);
    }

    //негативные сценарии

    @Test
    void getReviews_EmptyList_ShouldThrowException() {
        when(reviewRepository.findAll()).thenReturn(List.of());

        BusinessException ex = assertThrows(BusinessException.class,
                () -> reviewService.getReviews());

        assertEquals(" Возникла ошибка: Отзывы не найдены", ex.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, ex.getHttpStatus());
    }

    @Test
    void getReviewById_NotFound_ShouldThrowException() {
        when(reviewRepository.findById(999L)).thenReturn(Optional.empty());

        BusinessException ex = assertThrows(BusinessException.class,
                () -> reviewService.getReviewById(999L));

        assertEquals(" Возникла ошибка: Отзыв не найден", ex.getMessage());
    }

    @Test
    void updateReview_NotUserReview_ShouldThrowException() {
        User anotherUser = new User();
        anotherUser.setId(99L);
        anotherUser.setUsername("another_user");
        testReview.setUser_id(anotherUser);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("john_doe");
        when(userRepository.findUserByUsername("john_doe")).thenReturn(Optional.of(testUser));
        when(reviewRepository.findById(10L)).thenReturn(Optional.of(testReview));

        BusinessException ex = assertThrows(BusinessException.class,
                () -> reviewService.updateReview(10L, testReviewDTO));

        assertEquals(" Возникла ошибка: Это не ваш отзыв", ex.getMessage());
        assertEquals(HttpStatus.FORBIDDEN, ex.getHttpStatus());
    }

    @Test
    void delete_NotUserReview_ShouldThrowException() {
        User anotherUser = new User();
        anotherUser.setId(99L);
        testReview.setUser_id(anotherUser);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("john_doe");
        when(userRepository.findUserByUsername("john_doe")).thenReturn(Optional.of(testUser));
        when(reviewRepository.findById(10L)).thenReturn(Optional.of(testReview));

        BusinessException ex = assertThrows(BusinessException.class,
                () -> reviewService.delete(10L));

        assertEquals(" Возникла ошибка: Это не ваш отзыв!", ex.getMessage());
        assertEquals(HttpStatus.FORBIDDEN, ex.getHttpStatus());
    }

    // ========== VERIFY ПРОВЕРКИ ==========

    @Test
    void createReviewWithOrder_ShouldSetUserFromOrder() {
        when(reviewRepository.save(any(Review.class))).thenReturn(testReview);

        reviewService.createReviewWithOrder(testReviewDTO, testOrder);

        ArgumentCaptor<Review> reviewCaptor = ArgumentCaptor.forClass(Review.class);
        verify(reviewRepository).save(reviewCaptor.capture());

        Review capturedReview = reviewCaptor.getValue();
        assertEquals(testOrder.getUser_id(), capturedReview.getUser_id());
    }

    @Test
    void delete_ShouldNotDelete_WhenUserNotAuthorized() {
        User anotherUser = new User();
        anotherUser.setId(99L);
        testReview.setUser_id(anotherUser);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("john_doe");
        when(userRepository.findUserByUsername("john_doe")).thenReturn(Optional.of(testUser));
        when(reviewRepository.findById(10L)).thenReturn(Optional.of(testReview));

        assertThrows(BusinessException.class, () -> reviewService.delete(10L));

        verify(reviewRepository, never()).delete(any());
    }

    // ========== ARGUMENT CAPTOR ==========

    @Test
    void createReviewWithOrder_ShouldCaptureReviewData() {
        when(reviewRepository.save(any(Review.class))).thenReturn(testReview);

        reviewService.createReviewWithOrder(testReviewDTO, testOrder);

        ArgumentCaptor<Review> reviewCaptor = ArgumentCaptor.forClass(Review.class);
        verify(reviewRepository).save(reviewCaptor.capture());

        Review capturedReview = reviewCaptor.getValue();
        assertEquals(5, capturedReview.getRating());
        assertEquals("Great food!", capturedReview.getComment());
        assertEquals(testOrder, capturedReview.getOrder_id());
    }
}
