package com.foodDelivery.project.serviceTest;

import com.foodDelivery.project.domen.dto.ProductDTO;
import com.foodDelivery.project.domen.model.Product;
import com.foodDelivery.project.domen.responce.ProductToRetrieve;
import com.foodDelivery.project.exception.BusinessException;
import com.foodDelivery.project.repository.ProductRepository;
import com.foodDelivery.project.service.impl.ProductServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {
    @Mock
    private ProductRepository repository;

    @InjectMocks
    private ProductServiceImpl service;

    @Test
    void createProduct_shouldSave() {
        ProductDTO dto = new ProductDTO();
        dto.setName("Pizza");
        dto.setPrice(100);
        dto.setAmount(2);
        service.createProduct(dto);

        verify(repository).save(any(Product.class));
    }

    @Test
    void getProducts_shouldReturnMappedData() {
        Product product = new Product();
        product.setName("Burger");
        product.setPrice(50);
        product.setAmount(3);

        when(repository.findAll()).thenReturn(List.of(product));

        List<ProductToRetrieve> result = service.getProducts();

        assertEquals(1, result.size());
        assertEquals("Burger", result.get(0).getName());
    }

    @Test
    void getProducts_shouldThrowWhenEmpty() {
        when(repository.findAll()).thenReturn(List.of());

        assertThrows(BusinessException.class,
                () -> service.getProducts());
    }

    @Test
    void updateProduct_shouldUpdateFields() {
        Product product = new Product();
        product.setName("Old");

        when(repository.findById(1L)).thenReturn(Optional.of(product));
        when(repository.save(any())).thenReturn(product);

        ProductDTO dto = new ProductDTO();
        dto.setName("New");
        dto.setPrice(200);

        ProductDTO result = service.updateProduct(1L, dto);

        assertEquals("New", result.getName());
        assertEquals(200, result.getPrice());
    }

    @Test
    void deleteProduct_shouldRemove() {
        Product product = new Product();

        when(repository.findById(1L)).thenReturn(Optional.of(product));

        service.deleteProduct(1L);

        verify(repository).delete(product);
    }
}
