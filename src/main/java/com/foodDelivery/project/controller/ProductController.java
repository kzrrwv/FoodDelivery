package com.foodDelivery.project.controller;

import com.foodDelivery.project.domen.dto.ProductDTO;
import com.foodDelivery.project.domen.responce.ProductToRetrieve;
import com.foodDelivery.project.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/product")
@Tag(name = "Products", description = "Управление продуктами")
public class ProductController {

    private final ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping("/{warehouse_id}")
    @Operation(summary = "Создать продукт")
    public ResponseEntity<Void> addProduct(
            @RequestBody @Valid ProductDTO productDTO,
            @PathVariable Long warehouse_id){

        productService.createProduct(productDTO, warehouse_id);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping
    @Operation(summary = "Получить список продуктов")
    public ResponseEntity<List<ProductToRetrieve>> getProducts(){
        return ResponseEntity.ok(productService.getProducts());
    }

    @GetMapping("/page")
    @Operation(summary = "Получить список продуктов пагинация")
    public ResponseEntity<List<ProductToRetrieve>> getProductsWithPageable(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size){

        return ResponseEntity.ok(
                productService.findProductsWithPageable(PageRequest.of(page, size))
        );
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить продукт по ID")
    public ResponseEntity<ProductToRetrieve> getProductById(@PathVariable Long id){
        return ResponseEntity.ok(productService.getProductById(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Обновить продукт")
    public ResponseEntity<ProductDTO> updateProduct(
            @PathVariable Long id,
            @RequestBody @Valid ProductDTO productDTO){

        return ResponseEntity.ok(productService.updateProduct(id, productDTO));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить продукт")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id){
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
}
