package com.foodDelivery.project.serviceTest;

import com.foodDelivery.project.domen.dto.ProductDTO;
import com.foodDelivery.project.domen.model.Product;
import com.foodDelivery.project.domen.model.Warehouse;
import com.foodDelivery.project.domen.responce.ProductToRetrieve;
import com.foodDelivery.project.exception.BusinessException;
import com.foodDelivery.project.repository.ProductRepository;
import com.foodDelivery.project.repository.WarehouseRepository;
import com.foodDelivery.project.service.impl.ProductServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository repository;

    @Mock
    private WarehouseRepository warehouseRepository;

    @InjectMocks
    private ProductServiceImpl service;

    @Test
    void createProduct_success() {

        ProductDTO dto = new ProductDTO();
        dto.setName("Pizza");
        dto.setPrice(100);
        dto.setAmount(5);

        Warehouse warehouse = new Warehouse();
        warehouse.setProducts(new ArrayList<>());

        when(warehouseRepository.findById(1L))
                .thenReturn(Optional.of(warehouse));

        service.createProduct(dto, 1L);

        verify(repository).save(any(Product.class));
    }

    @Test
    void getProductById_success() {

        Product product = new Product();
        product.setName("Pizza");

        when(repository.findById(1L))
                .thenReturn(Optional.of(product));

        ProductToRetrieve result = service.getProductById(1L);

        assertNotNull(result);
    }

    @Test
    void findProductsWithPageable_success() {

        Product product = new Product();

        when(repository.findAll(any(PageRequest.class)))
                .thenReturn(new PageImpl<>(List.of(product)));

        List<ProductToRetrieve> result =
                service.findProductsWithPageable(PageRequest.of(0, 5));

        assertEquals(1, result.size());
    }

    @Test
    void updateProduct_success() {

        Product product = new Product();

        when(repository.findById(1L))
                .thenReturn(Optional.of(product));

        when(repository.save(any(Product.class)))
                .thenReturn(product);

        ProductDTO dto = new ProductDTO();
        dto.setName("Burger");

        ProductDTO result = service.updateProduct(1L, dto);

        assertNotNull(result);
    }

    @Test
    void deleteProduct_notFound() {

        when(repository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(BusinessException.class,
                () -> service.deleteProduct(1L));
    }
}