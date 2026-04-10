package com.foodDelivery.project.controller;

import com.foodDelivery.project.domen.dto.ReviewDTO;
import com.foodDelivery.project.domen.responce.ReviewToRetrieve;
import com.foodDelivery.project.service.ReviewService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/review")
public class ReviewController {

    private final ReviewService reviewService;

    @Autowired
    public ReviewController(ReviewService reviewService){
        this.reviewService = reviewService;
    }

    @PostMapping
    public ResponseEntity<Void> addReviews(@RequestBody @Valid ReviewDTO reviewDTO){
        reviewService.createReview(reviewDTO);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping
    public ResponseEntity<List<ReviewToRetrieve>> getReviews(@RequestParam(defaultValue = "0") Integer page, @RequestParam(defaultValue = "10") Integer size){
        reviewService.findReviewsWithPageble(PageRequest.of(page, size));
        return null;
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReviewDTO> getReviewById(@PathVariable Long id){
        return ResponseEntity.ok(new ReviewDTO());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReviewDTO> updateReviewPagination(@PathVariable Long id, @RequestBody ReviewDTO reviewDTO){
        ReviewDTO updated = reviewService.updateReview(id, reviewDTO);
        return ResponseEntity.ok(updated);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<ReviewDTO> updateReview(@PathVariable Long id, @RequestBody ReviewDTO reviewDTO){
        ReviewDTO updated = reviewService.updateReview(id, reviewDTO);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReview(@PathVariable Long id){
        reviewService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
