package com.foodDelivery.project.service.impl;

import com.foodDelivery.project.domen.dto.ReviewDTO;
import com.foodDelivery.project.domen.model.Review;
import com.foodDelivery.project.domen.responce.ReviewToRetrieve;
import com.foodDelivery.project.exception.BusinessException;
import com.foodDelivery.project.repository.ReviewRepository;
import com.foodDelivery.project.service.ReviewService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ReviewServiceImpl implements ReviewService {

    private ReviewRepository repository;

    private static final Logger log = LoggerFactory.getLogger(ReviewServiceImpl.class);
    @Autowired
    public ReviewServiceImpl(ReviewRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<ReviewToRetrieve> getReviews(){
        List<Review> all = repository.findAll();

        if (all.isEmpty()) {
            log.debug("База данных пустая!");
            throw new BusinessException(
                    "Отзывы отсутствуют",
                    HttpStatus.NOT_FOUND
            );
        }

        List<ReviewToRetrieve> reviewToRetrieves = new ArrayList<>();

        for(Review review : all){
            ReviewToRetrieve reviewToRetrieve = new ReviewToRetrieve();
            reviewToRetrieve.setComment(review.getComment());
            reviewToRetrieve.setRating(review.getRating());
            reviewToRetrieves.add(reviewToRetrieve);
        }
        return reviewToRetrieves;
    }

    @Override
    public void saveReviews(ReviewDTO reviewDTO){
        Review review = new Review();
        review.setRating(reviewDTO.getRating());
        review.setComment(reviewDTO.getComment());
        repository.save(review);
        log.info("Отзыв успешно добавлен.");
    }

    @Override
    public List<ReviewToRetrieve> findReviewsWithPageble(PageRequest of) {
        Page<Review> all = repository.findAll(of);
        List<Review> content = all.getContent();
        return null;
    }
}
