package com.foodDelivery.project.service;

import com.foodDelivery.project.domen.dto.ReviewDTO;
import com.foodDelivery.project.domen.model.Order;
import com.foodDelivery.project.domen.model.Review;
import com.foodDelivery.project.domen.responce.ReviewToRetrieve;
import org.springframework.data.domain.PageRequest;

import java.util.List;

public interface ReviewService {

    List<ReviewToRetrieve> findReviewsWithPageble(PageRequest of);

    List<ReviewToRetrieve> getReviews();

    ReviewDTO updateReview(Long id, ReviewDTO reviewDTO);

    void delete(Long id);

    ReviewToRetrieve getReviewById(Long id);

    Review createReviewWithOrder(ReviewDTO reviewDTO, Order order);

    void createReview(ReviewDTO reviewDTO, Long orderId);
}
