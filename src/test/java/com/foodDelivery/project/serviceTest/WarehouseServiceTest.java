package com.foodDelivery.project.serviceTest;

import com.foodDelivery.project.domen.dto.WarehouseDTO;
import com.foodDelivery.project.domen.model.Product;
import com.foodDelivery.project.domen.model.Warehouse;
import com.foodDelivery.project.exception.BusinessException;
import com.foodDelivery.project.repository.ProductRepository;
import com.foodDelivery.project.repository.WarehouseRepository;
import com.foodDelivery.project.service.impl.WarehouseServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WarehouseServiceTest {

    @Mock
    private WarehouseRepository warehouseRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private WarehouseServiceImpl service;

    @Test
    void createWarehouse_success() {

        WarehouseDTO dto = new WarehouseDTO();
        dto.setProducts(new ArrayList<>());

        service.createWarehouse(dto);

        verify(warehouseRepository).save(any(Warehouse.class));
    }

    @Test
    void getWarehouses_success() {

        when(warehouseRepository.findAllId())
                .thenReturn(List.of(1L, 2L));

        List<Long> result = service.getWarehouses();

        assertEquals(2, result.size());
    }

    @Test
    void getProductsById_success() {

        Warehouse warehouse = new Warehouse();
        warehouse.setProducts(List.of(new Product()));

        when(warehouseRepository.findById(1L))
                .thenReturn(Optional.of(warehouse));

        List<Product> result = service.getProductsById(1L);

        assertEquals(1, result.size());
    }

    @Test
    void updateWarehouse_success() {

        Warehouse warehouse = new Warehouse();

        when(warehouseRepository.findById(1L))
                .thenReturn(Optional.of(warehouse));

        when(warehouseRepository.save(any(Warehouse.class)))
                .thenReturn(warehouse);

        WarehouseDTO dto = new WarehouseDTO();
        dto.setProducts(new ArrayList<>());

        WarehouseDTO result = service.updateWarehouse(1L, dto);

        assertNotNull(result);
    }

    @Test
    void deleteWarehouse_notFound() {

        when(warehouseRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(BusinessException.class,
                () -> service.deleteWarehouse(1L));
    }
}