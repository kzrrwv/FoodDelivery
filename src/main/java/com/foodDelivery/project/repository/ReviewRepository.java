package com.foodDelivery.project.repository;

import com.foodDelivery.project.domen.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    //получение отзывов по продукту, отсортированные по дате
    @Query(value = "SELECT * FROM reviews WHERE product_id = :productId ORDER BY created_at DESC", nativeQuery = true)
    List<Review> findReviewsByProductIdOrderByCreatedAtDesc(@Param("productId") Long productId);
}
