package com.foodDelivery.project.serviceTest;

import com.foodDelivery.project.domen.dto.ProductDTO;
import com.foodDelivery.project.domen.model.Product;
import com.foodDelivery.project.domen.model.Warehouse;
import com.foodDelivery.project.domen.responce.ProductToRetrieve;
import com.foodDelivery.project.exception.BusinessException;
import com.foodDelivery.project.repository.ProductRepository;
import com.foodDelivery.project.repository.WarehouseRepository;
import com.foodDelivery.project.service.impl.ProductServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private WarehouseRepository warehouseRepository;

    @InjectMocks
    private ProductServiceImpl productService;

    private Product testProduct;
    private Warehouse testWarehouse;

    @BeforeEach
    void setUp() {
        testProduct = new Product();
        testProduct.setId(1L);
        testProduct.setName("Classic Burger");
        testProduct.setPrice(250);
        testProduct.setAmount(50);

        testWarehouse = new Warehouse();
        testWarehouse.setId(1L);
        testWarehouse.setProducts(new ArrayList<>());
    }

    @Test
    void shouldCreateProduct_whenValidData() {
        // Arrange
        ProductDTO productDTO = new ProductDTO();
        productDTO.setName("New Product");
        productDTO.setPrice(300);
        productDTO.setAmount(20);

        when(warehouseRepository.findById(1L)).thenReturn(Optional.of(testWarehouse));
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);

        // Act
        productService.createProduct(productDTO, 1L);

        // Assert
        verify(productRepository).save(any(Product.class));
        verify(warehouseRepository).save(testWarehouse);
    }

    @Test
    void shouldThrowException_whenProductNotFoundForUpdate() {
        // Arrange
        ProductDTO updateDTO = new ProductDTO();
        updateDTO.setName("Updated");
        updateDTO.setPrice(200);
        updateDTO.setAmount(10);

        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class,
                () -> productService.updateProduct(999L, updateDTO));

        assertThat(exception.getMessage()).contains("Продукт не найден");
    }

    @Test
    void shouldGetAllProducts_whenProductsExist() {
        // Arrange
        when(productRepository.findAll()).thenReturn(List.of(testProduct));

        // Act
        List<ProductToRetrieve> result = productService.getProducts();

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Classic Burger");
    }

    @Test
    void shouldThrowException_whenNoProductsExist() {
        // Arrange
        when(productRepository.findAll()).thenReturn(List.of());

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class,
                () -> productService.getProducts());

        assertThat(exception.getMessage()).contains("Продукты не найдены");
        assertThat(exception.getHttpStatus()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @ParameterizedTest
    @CsvSource({
            "10, 50, 40",
            "1, 100, 99",
            "0, 20, 20"
    })
    void shouldUpdateAmountCorrectly(int newAmount, int currentAmount, int expected) {
        // Arrange
        testProduct.setAmount(currentAmount);
        ProductDTO updateDTO = new ProductDTO();
        updateDTO.setAmount(newAmount);
        updateDTO.setName("Updated Product");
        updateDTO.setPrice(100);

        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);

        // Act
        productService.updateProduct(1L, updateDTO);

        // Assert
        assertThat(testProduct.getAmount()).isEqualTo(expected);
    }
}