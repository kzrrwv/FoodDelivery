package com.foodDelivery.project.service;

import com.foodDelivery.project.domen.dto.WarehouseDTO;
import com.foodDelivery.project.domen.model.Warehouse;

import java.util.List;

public interface WarehouseService {

    void createWarehouse(Long warehouseId, WarehouseDTO warehouseDTO);

    List<Warehouse> getWarehouses();

    WarehouseDTO updateWarehouse(Long id, WarehouseDTO warehouseDTO);

    void deleteWarehouse(Long id);
}
