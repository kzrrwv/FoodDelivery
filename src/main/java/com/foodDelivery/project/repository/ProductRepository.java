package com.foodDelivery.project.repository;

import com.foodDelivery.project.domen.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    //получить продукты по категории отсортированные по цене
    @Query(value = "SELECT * FROM products WHERE category_id = :categoryId ORDER BY price ASC", nativeQuery = true)
    List<Product> findByCategoryOrderByPriceAsc(@Param("categoryId") Long categoryId);
}
