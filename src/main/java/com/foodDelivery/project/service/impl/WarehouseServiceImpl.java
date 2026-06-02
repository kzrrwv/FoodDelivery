package com.foodDelivery.project.service.impl;

import com.foodDelivery.project.domen.dto.WarehouseDTO;
import com.foodDelivery.project.domen.model.Product;
import com.foodDelivery.project.domen.model.Warehouse;
import com.foodDelivery.project.exception.BusinessException;
import com.foodDelivery.project.repository.WarehouseRepository;
import com.foodDelivery.project.service.WarehouseService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@PreAuthorize(value = "hasRole('ROLE_ADMIN')")
public class WarehouseServiceImpl implements WarehouseService {

    private WarehouseRepository warehouseRepository;

    public WarehouseServiceImpl(WarehouseRepository warehouseRepository) {
        this.warehouseRepository = warehouseRepository;
    }

    @Override
    public void createWarehouse(WarehouseDTO warehouseDTO) {
        Warehouse warehouse = new Warehouse();

        warehouse.setProducts(warehouseDTO.getProducts());

        warehouseRepository.save(warehouse);
    }

    @Override
    public List<Long> getWarehouses() {
        List<Long> allId = warehouseRepository.findAllId();
        return allId;
    }

    @Override
    public List<Product> getProductsById(Long id) {
        List<Product> allProducts = warehouseRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Склад не найден!", HttpStatus.NOT_FOUND)).getProducts();
        return allProducts;
    }

    @Override
    public WarehouseDTO updateWarehouse(Long id, WarehouseDTO warehouseDTO) {
        Warehouse warehouse = warehouseRepository.findById(id)
                .orElseThrow(() -> new BusinessException(
                        "Склад не найден!",
                        HttpStatus.NOT_FOUND));

        warehouse.setProducts(warehouseDTO.getProducts());
        warehouse.setId(id);

        Warehouse saved = warehouseRepository.save(warehouse);

        WarehouseDTO dto = new WarehouseDTO();

        dto.setProducts(saved.getProducts());
        return dto;
    }

    @Override
    public void deleteWarehouse(Long id) {
        Warehouse warehouse = warehouseRepository.findById(id)
                .orElseThrow(() ->
                        new BusinessException(
                                "Склад не найден!",
                                HttpStatus.NOT_FOUND));

        warehouseRepository.delete(warehouse);
    }
}
