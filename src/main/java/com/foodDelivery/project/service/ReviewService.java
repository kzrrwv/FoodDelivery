package com.foodDelivery.project.service;

import com.foodDelivery.project.domen.dto.ReviewDTO;
import com.foodDelivery.project.domen.responce.ReviewToRetrieve;
import org.springframework.data.domain.PageRequest;

import java.util.List;

public interface ReviewService {

    List<ReviewToRetrieve> findReviewsWithPageble(PageRequest of);

    List<ReviewToRetrieve> getReviews();

    public void saveReviews(ReviewDTO reviewDTO);
}
