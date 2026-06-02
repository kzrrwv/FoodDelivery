package com.foodDelivery.project.service.impl;

import com.foodDelivery.project.domen.dto.ReviewDTO;
import com.foodDelivery.project.domen.model.Order;
import com.foodDelivery.project.domen.model.Review;
import com.foodDelivery.project.domen.model.User;
import com.foodDelivery.project.domen.responce.ReviewToRetrieve;
import com.foodDelivery.project.exception.BusinessException;
import com.foodDelivery.project.repository.ReviewRepository;
import com.foodDelivery.project.repository.UserRepository;
import com.foodDelivery.project.service.ReviewService;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
//все методы изменения review сделать по пользователю
//поменять preAuthorize
@Service
@PreAuthorize(value = "hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
public class ReviewServiceImpl implements ReviewService {

    private ReviewRepository repository;

    private final UserRepository userRepository;

    private static final Logger log = LoggerFactory.getLogger(ReviewServiceImpl.class);
    @Autowired
    public ReviewServiceImpl(ReviewRepository repository, UserRepository userRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
    }

    private User getCurrentUser(){
        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();
        String username = authentication.getName();
        return userRepository.findUserByUsername(username)
                .orElseThrow(() -> new BusinessException(
                        "Пользователь не найден!",
                        HttpStatus.NOT_FOUND
                ));
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('ROLE_USER')")
    public Review createReviewWithOrder(ReviewDTO reviewDTO, Order order) {
        Review review = new Review();

        review.setRating(reviewDTO.getRating());
        review.setComment(reviewDTO.getComment());
        review.setOrder_id(order);
        review.setUser_id(order.getUser_id());

        Review saved = repository.save(review);
        log.info("Отзыв успешно создан вместе с заказом");

        return saved;
    }

    @Override
    @PreAuthorize("isAuthenticated()")
    public List<ReviewToRetrieve> getReviews(){
        List<Review> all = repository.findAll();

        if(all.isEmpty()){
            throw new BusinessException(
                    "Отзывы не найдены",
                    HttpStatus.NOT_FOUND );
        }

        List<ReviewToRetrieve> reviewToRetrieves = new ArrayList<>();

        for(Review review : all){
            ReviewToRetrieve reviewToRetrieve = new ReviewToRetrieve();

            reviewToRetrieve.setComment(review.getComment());
            reviewToRetrieve.setRating(review.getRating());

            reviewToRetrieves.add(reviewToRetrieve);
        }

        log.info("Список отзывов успешно получен");
        return reviewToRetrieves;
    }

    @Override
    @PreAuthorize("isAuthenticated()")
    public ReviewToRetrieve getReviewById(Long id) {
        Review review = repository.findById(id)
                .orElseThrow(() -> new BusinessException(
                        "Отзыв не найден",
                        HttpStatus.NOT_FOUND
                ));

        ReviewToRetrieve dto = new ReviewToRetrieve();

        dto.setComment(review.getComment());
        dto.setRating(review.getRating());

        log.info("Отзыв с id {} успешно получен!", id);
        return dto;
    }

    @Override
    @PreAuthorize("isAuthenticated()")
    public List<ReviewToRetrieve> findReviewsWithPageble(PageRequest of) {
        Page<Review> page = repository.findAll(of);

        List<ReviewToRetrieve> result = new ArrayList<>();

        for (Review review : page.getContent()) {
            ReviewToRetrieve dto = new ReviewToRetrieve();

            dto.setComment(review.getComment());
            dto.setRating(review.getRating());

            result.add(dto);
        }

        log.info("Список отзывов успешно получен!");
        return result;
    }

    @Override
    @PreAuthorize("hasRole('ROLE_USER')")
    public ReviewDTO updateReview(Long id, ReviewDTO reviewDTO) {
        Review review = repository.findById(id)
                .orElseThrow(() -> new BusinessException(
                        "Отзыв не найден",
                        HttpStatus.NOT_FOUND
                ));

        User currentUser = getCurrentUser();

        if (!review.getUser_id().getId().equals(currentUser.getId())) {
            throw new BusinessException(
                    "Это не ваш отзыв",
                    HttpStatus.FORBIDDEN );
        }

        review.setComment(reviewDTO.getComment());
        review.setRating(reviewDTO.getRating());
        review.setCreatedAt(reviewDTO.getCreatedAt());

        Review saved = repository.save(review);

        ReviewDTO dto = new ReviewDTO();

        dto.setComment(saved.getComment());
        dto.setRating(saved.getRating());
        dto.setCreatedAt(saved.getCreatedAt());

        log.info("Отзыв с id {} успешно обновлен", id);
        return dto;
    }

    @Override
    @PreAuthorize("hasRole('ROLE_USER')")
    public void delete(Long id) {
        Review review = repository.findById(id)
                .orElseThrow(() -> new BusinessException("Отзыв не найден!", HttpStatus.NOT_FOUND));

        User currentUser = getCurrentUser();

        if(!review.getUser_id().getId().equals(currentUser.getId())){
            throw new BusinessException(
                    "Это не ваш отзыв!",
                    HttpStatus.FORBIDDEN
            );
        }
        repository.delete(review);

        log.info("Отзыв с id {} успешно удален.", id);
    }
}
