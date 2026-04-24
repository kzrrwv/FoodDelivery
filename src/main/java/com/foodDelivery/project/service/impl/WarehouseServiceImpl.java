package com.foodDelivery.project.service.impl;

import com.foodDelivery.project.domen.dto.WarehouseDTO;
import com.foodDelivery.project.domen.model.Warehouse;
import com.foodDelivery.project.service.WarehouseService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@PreAuthorize(value = "hasRole('ROLE_ADMIN')")
public class WarehouseServiceImpl implements WarehouseService {
    @Override
    public void createWarehouse(WarehouseDTO warehouseDTO) {

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
