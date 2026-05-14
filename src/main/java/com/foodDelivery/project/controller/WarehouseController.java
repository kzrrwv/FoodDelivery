package com.foodDelivery.project.controller;

import com.foodDelivery.project.domen.dto.WarehouseDTO;
import com.foodDelivery.project.domen.model.Product;
import com.foodDelivery.project.service.WarehouseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/warehouse")
@Tag(name = "Warehouse", description = "Склады")
public class WarehouseController {
    private final WarehouseService warehouseService;

    public WarehouseController(WarehouseService warehouseService) {
        this.warehouseService = warehouseService;
    }

    @PostMapping
    @Operation(summary = "Создать склад")
    public ResponseEntity<Void> addWarehouse(@RequestBody @Valid WarehouseDTO warehouseDTO, @PathVariable Long id){
        warehouseService.createWarehouse(id, warehouseDTO);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping
    @Operation(summary = "Получить все склады")
    public ResponseEntity<List<Long>> getWarehouses(){
        return ResponseEntity.ok(warehouseService.getWarehouses());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить продукты по ID склада")
    public ResponseEntity<List<Product>> getProductsByIdWarehouse(@PathVariable Long id){
        return ResponseEntity.ok(warehouseService.getProductsById(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Обновить склад")
    public ResponseEntity<WarehouseDTO> updateWarehouse(@PathVariable Long id, @RequestBody @Valid WarehouseDTO warehouseDTO){
        return ResponseEntity.ok(warehouseService.updateWarehouse(id, warehouseDTO));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить склад")
    public ResponseEntity<Void> deleteWarehouse(@PathVariable Long id){
        warehouseService.deleteWarehouse(id);
        return ResponseEntity.noContent().build();
    }

}
