package com.foodDelivery.project.service;

import com.foodDelivery.project.domen.dto.ProductDTO;
import com.foodDelivery.project.domen.responce.ProductToRetrieve;
import com.foodDelivery.project.domen.responce.ReviewToRetrieve;
import org.springframework.data.domain.PageRequest;

import java.util.List;

public interface ProductService {

    List<ProductToRetrieve> getProducts();

    void saveProducts(ProductDTO productDTO);

    List<ProductToRetrieve> findReviewsWithPageable(PageRequest of);
}
