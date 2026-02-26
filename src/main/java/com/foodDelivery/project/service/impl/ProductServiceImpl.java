package com.foodDelivery.project.service.impl;

import com.foodDelivery.project.domen.dto.ProductDTO;
import com.foodDelivery.project.domen.model.Product;
import com.foodDelivery.project.domen.responce.ProductToRetrieve;
import com.foodDelivery.project.exception.BusinessException;
import com.foodDelivery.project.repository.ProductRepository;
import com.foodDelivery.project.service.ProductService;
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
public class ProductServiceImpl implements ProductService {

    private ProductRepository repository;

    private static final Logger log = LoggerFactory.getLogger(ProductServiceImpl.class);

    @Autowired
    public ProductServiceImpl(ProductRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<ProductToRetrieve> getProducts(){
        List<Product> all = repository.findAll();

        if (all.isEmpty()) {
            log.debug("База данных пустая!");
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
        Product product = new Product();
        product.setAmount(productDTO.getAmount());
        product.setName(productDTO.getName());
        product.setPrice(productDTO.getPrice());
        repository.save(product);
        log.info("Продукт успешно добавлен.");
    }

    @Override
    public List<ProductToRetrieve> findProductsWithPageable(PageRequest of) {
        Page<Product> all = repository.findAll(of);
        List<Product> content = all.getContent();
        return null;
    }
}
