package com.foodDelivery.project.controller;

import com.foodDelivery.project.domen.dto.ProductDTO;
import com.foodDelivery.project.domen.responce.ProductToRetrieve;
import com.foodDelivery.project.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/product")
public class ProductController {

    private final ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public ResponseEntity<List<ProductToRetrieve>> getProducts(){
        return ResponseEntity.ok(productService.getProducts());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> getProductById(@PathVariable int id){
        return ResponseEntity.ok(new ProductDTO());
    }

    @PostMapping
    public ResponseEntity<Void> addProduct(@RequestBody ProductDTO productDTO){
        productService.saveProducts(productDTO);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
