package com.foodDelivery.project.service;

import com.foodDelivery.project.domen.dto.ProductDTO;
import com.foodDelivery.project.domen.responce.ProductToRetrieve;

import java.util.List;

public interface ProductService {

    List<ProductToRetrieve> getProducts();

    void saveProducts(ProductDTO productDTO);
}
