package com.foodDelivery.project.service.impl;

import com.foodDelivery.project.domen.dto.ProductDTO;
import com.foodDelivery.project.domen.dto.WarehouseDTO;
import com.foodDelivery.project.domen.model.Product;
import com.foodDelivery.project.domen.model.Warehouse;
import com.foodDelivery.project.repository.ProductRepository;
import com.foodDelivery.project.repository.WarehouseRepository;
import com.foodDelivery.project.service.WarehouseService;
import jakarta.transaction.Transactional;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@PreAuthorize(value = "hasRole('ROLE_ADMIN')")
public class WarehouseServiceImpl implements WarehouseService {

    private WarehouseRepository warehouseRepository;
    private ProductRepository productRepository;

    @Override
    public void createWarehouse(Long warehouseId, WarehouseDTO warehouseDTO) {

    }

    @Override
    public List<Warehouse> getWarehouses() {
        return List.of();
    }

    @Override
    public WarehouseDTO updateWarehouse(Long id, WarehouseDTO warehouseDTO) {
        return null;
    }

    @Override
    public void deleteWarehouse(Long id) {

    }
}
