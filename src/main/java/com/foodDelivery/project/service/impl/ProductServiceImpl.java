package com.foodDelivery.project.service.impl;

import com.foodDelivery.project.domen.dto.ProductDTO;
import com.foodDelivery.project.domen.model.Product;
import com.foodDelivery.project.domen.responce.ProductToRetrieve;
import com.foodDelivery.project.exception.BusinessException;
import com.foodDelivery.project.repository.ProductRepository;
import com.foodDelivery.project.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    private ProductRepository repository;

    @Autowired
    public ProductServiceImpl(ProductRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<ProductToRetrieve> getProducts(){
        List<Product> all = repository.findAll();

        if (all.isEmpty()) {
            throw new BusinessException(
                    "Продукты не найдены",
                    HttpStatus.NOT_FOUND
            );
        }

        List<ProductToRetrieve> productToRetrieves = new ArrayList<>();
        for(Product product : all){
            ProductToRetrieve productToRetrieve = new ProductToRetrieve();
            productToRetrieve.setAmount(product.getAmount());
            productToRetrieve.setName(product.getName());
            productToRetrieve.setPrice(product.getPrice());
            productToRetrieves.add(productToRetrieve);
        }
        return productToRetrieves;
    }

    @Override
    public void saveProducts(ProductDTO productDTO){
        if (productDTO.getPrice() <= 0) {
            throw new BusinessException(
                    "Цена должна быть больше 0",
                    HttpStatus.BAD_REQUEST
            );
        }

        if (productDTO.getAmount() < 0) {
            throw new BusinessException(
                    "Количество не может быть отрицательным",
                    HttpStatus.BAD_REQUEST
            );
        }

        Product product = new Product();
        product.setAmount(productDTO.getAmount());
        product.setName(productDTO.getName());
        product.setPrice(productDTO.getPrice());
        repository.save(product);
    }
}
