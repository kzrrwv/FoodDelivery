package com.foodDelivery.project.controller;

import com.foodDelivery.project.domen.dto.ProductDTO;
import com.foodDelivery.project.domen.dto.UserDTO;
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

    @PostMapping
    public ResponseEntity<Void> addProduct(@RequestBody @Valid ProductDTO productDTO){
        productService.createProduct(productDTO);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping
    public ResponseEntity<List<ProductToRetrieve>> getProducts(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size){

        return ResponseEntity.ok(
                productService.findProductsWithPageable(PageRequest.of(page, size))
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductToRetrieve> getProductById(@PathVariable Long id){
        return ResponseEntity.ok(productService.getProductById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductDTO> updateProduct(
            @PathVariable Long id,
            @RequestBody @Valid ProductDTO productDTO){

        return ResponseEntity.ok(productService.updateProduct(id, productDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id){
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
}
