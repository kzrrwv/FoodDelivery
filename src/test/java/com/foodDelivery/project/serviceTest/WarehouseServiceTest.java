package com.foodDelivery.project.serviceTest;

import com.foodDelivery.project.domen.dto.WarehouseDTO;
import com.foodDelivery.project.domen.model.Product;
import com.foodDelivery.project.domen.model.Warehouse;
import com.foodDelivery.project.exception.BusinessException;
import com.foodDelivery.project.repository.WarehouseRepository;
import com.foodDelivery.project.service.impl.WarehouseServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WarehouseServiceTest {

    @Mock
    private WarehouseRepository warehouseRepository;

    @InjectMocks
    private WarehouseServiceImpl warehouseService;

    private Warehouse testWarehouse;
    private WarehouseDTO testWarehouseDTO;
    private Product testProduct;

    @BeforeEach
    void setUp() {
        testProduct = new Product();
        testProduct.setId(1L);
        testProduct.setName("Test Product");

        testWarehouse = new Warehouse();
        testWarehouse.setId(1L);
        testWarehouse.setProducts(new ArrayList<>());
        testWarehouse.getProducts().add(testProduct);

        testWarehouseDTO = new WarehouseDTO();
        testWarehouseDTO.setProducts(List.of(testProduct));
    }

    //позитивные сценарии

    @Test
    void createWarehouse_Success_ShouldCreateWarehouse() {
        when(warehouseRepository.save(any(Warehouse.class))).thenReturn(testWarehouse);

        assertDoesNotThrow(() -> warehouseService.createWarehouse(testWarehouseDTO));

        verify(warehouseRepository).save(any(Warehouse.class));
    }

    @Test
    void getWarehouses_Success_ShouldReturnListOfIds() {
        List<Long> ids = List.of(1L, 2L, 3L);
        when(warehouseRepository.findAllId()).thenReturn(ids);

        List<Long> result = warehouseService.getWarehouses();

        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals(1L, result.get(0));
        verify(warehouseRepository).findAllId();
    }

    @Test
    void getProductsById_Success_ShouldReturnProducts() {
        when(warehouseRepository.findById(1L)).thenReturn(Optional.of(testWarehouse));

        List<Product> result = warehouseService.getProductsById(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Test Product", result.get(0).getName());
        verify(warehouseRepository).findById(1L);
    }

    @Test
    void updateWarehouse_Success_ShouldUpdateAndReturnDTO() {
        when(warehouseRepository.findById(1L)).thenReturn(Optional.of(testWarehouse));
        when(warehouseRepository.save(any(Warehouse.class))).thenReturn(testWarehouse);

        WarehouseDTO result = warehouseService.updateWarehouse(1L, testWarehouseDTO);

        assertNotNull(result);
        assertNotNull(result.getProducts());
        verify(warehouseRepository).save(testWarehouse);
    }

    @Test
    void deleteWarehouse_Success_ShouldDeleteWarehouse() {
        when(warehouseRepository.findById(1L)).thenReturn(Optional.of(testWarehouse));

        assertDoesNotThrow(() -> warehouseService.deleteWarehouse(1L));

        verify(warehouseRepository).delete(testWarehouse);
    }

    //негативные сценарии

    @Test
    void getProductsById_WarehouseNotFound_ShouldThrowException() {
        when(warehouseRepository.findById(999L)).thenReturn(Optional.empty());

        BusinessException ex = assertThrows(BusinessException.class,
                () -> warehouseService.getProductsById(999L));

        assertEquals(" Возникла ошибка: Склад не найден!", ex.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, ex.getHttpStatus());
    }

    @Test
    void updateWarehouse_WarehouseNotFound_ShouldThrowException() {
        when(warehouseRepository.findById(999L)).thenReturn(Optional.empty());

        BusinessException ex = assertThrows(BusinessException.class,
                () -> warehouseService.updateWarehouse(999L, testWarehouseDTO));

        assertEquals(" Возникла ошибка: Склад не найден!", ex.getMessage());
    }

    @Test
    void deleteWarehouse_WarehouseNotFound_ShouldThrowException() {
        when(warehouseRepository.findById(999L)).thenReturn(Optional.empty());

        BusinessException ex = assertThrows(BusinessException.class,
                () -> warehouseService.deleteWarehouse(999L));

        assertEquals(" Возникла ошибка: Склад не найден!", ex.getMessage());
    }

    @Test
    void getWarehouses_EmptyList_ShouldReturnEmptyList() {
        when(warehouseRepository.findAllId()).thenReturn(List.of());

        List<Long> result = warehouseService.getWarehouses();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    //verify
    @Test
    void updateWarehouse_ShouldSaveWithCorrectId() {
        when(warehouseRepository.findById(1L)).thenReturn(Optional.of(testWarehouse));
        when(warehouseRepository.save(any(Warehouse.class))).thenReturn(testWarehouse);

        warehouseService.updateWarehouse(1L, testWarehouseDTO);

        ArgumentCaptor<Warehouse> warehouseCaptor = ArgumentCaptor.forClass(Warehouse.class);
        verify(warehouseRepository).save(warehouseCaptor.capture());

        assertEquals(1L, warehouseCaptor.getValue().getId());
    }

    //argument captor

    @Test
    void createWarehouse_ShouldCaptureWarehouseData() {
        when(warehouseRepository.save(any(Warehouse.class))).thenReturn(testWarehouse);

        warehouseService.createWarehouse(testWarehouseDTO);

        ArgumentCaptor<Warehouse> warehouseCaptor = ArgumentCaptor.forClass(Warehouse.class);
        verify(warehouseRepository).save(warehouseCaptor.capture());

        Warehouse capturedWarehouse = warehouseCaptor.getValue();
        assertNotNull(capturedWarehouse.getProducts());
        assertEquals(1, capturedWarehouse.getProducts().size());
        assertEquals("Test Product", capturedWarehouse.getProducts().get(0).getName());
    }

    @Test
    void updateWarehouse_ShouldCaptureUpdatedWarehouseData() {
        when(warehouseRepository.findById(1L)).thenReturn(Optional.of(testWarehouse));
        when(warehouseRepository.save(any(Warehouse.class))).thenReturn(testWarehouse);

        warehouseService.updateWarehouse(1L, testWarehouseDTO);

        ArgumentCaptor<Warehouse> warehouseCaptor = ArgumentCaptor.forClass(Warehouse.class);
        verify(warehouseRepository).save(warehouseCaptor.capture());

        Warehouse capturedWarehouse = warehouseCaptor.getValue();
        assertEquals(1L, capturedWarehouse.getId());
        assertNotNull(capturedWarehouse.getProducts());
    }
}