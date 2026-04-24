package com.foodDelivery.project.service;

import com.foodDelivery.project.domen.dto.ProductDTO;
import com.foodDelivery.project.domen.responce.ProductToRetrieve;
import com.foodDelivery.project.domen.responce.ReviewToRetrieve;
import org.springframework.data.domain.PageRequest;

import java.util.List;

public interface ProductService {

    List<ProductToRetrieve> getProducts();

    void createProduct(ProductDTO productDTO);

    List<ProductToRetrieve> findProductsWithPageable(PageRequest of);

    ProductDTO updateProduct(Long id, ProductDTO productDTO);

    void deleteProduct(Long id);

    ProductToRetrieve getProductById(Long id);
}
