package com.foodDelivery.project.controller;

import com.foodDelivery.project.domen.dto.ProductDTO;
import com.foodDelivery.project.domen.responce.ProductToRetrieve;
import com.foodDelivery.project.domen.responce.ReviewToRetrieve;
import com.foodDelivery.project.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
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
    public ResponseEntity<List<ProductToRetrieve>> getProducts(@RequestParam(defaultValue = "0") Integer page, @RequestParam(defaultValue = "10") Integer size){
        productService.findProductsWithPageable(PageRequest.of(page, size));
        return null;
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> getProductById(@PathVariable int id){
        return ResponseEntity.ok(new ProductDTO());
    }

    @PostMapping
    public ResponseEntity<Void> addProduct(@RequestBody @Valid ProductDTO productDTO){
        productService.saveProducts(productDTO);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
