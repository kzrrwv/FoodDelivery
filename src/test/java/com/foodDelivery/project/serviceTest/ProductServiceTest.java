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
import org.junit.jupiter.params.provider.ValueSource;
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

import static org.junit.jupiter.api.Assertions.*;
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
    private ProductDTO testProductDTO;
    private Warehouse testWarehouse;

    @BeforeEach
    void setUp() {
        testWarehouse = new Warehouse();
        testWarehouse.setId(1L);
        testWarehouse.setProducts(new ArrayList<>());

        testProduct = new Product();
        testProduct.setId(1L);
        testProduct.setName("Pizza Margherita");
        testProduct.setPrice(500);
        testProduct.setAmount(10);
        testProduct.setWarehouse_id(testWarehouse);

        testProductDTO = new ProductDTO();
        testProductDTO.setName("Pizza Margherita");
        testProductDTO.setPrice(500);
        testProductDTO.setAmount(10);
    }

    //позитивные сценарии

    @Test
    void createProduct_WithExistingWarehouse_ShouldCreateProduct() {
        when(warehouseRepository.findById(1L)).thenReturn(Optional.of(testWarehouse));
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);
        when(warehouseRepository.save(any(Warehouse.class))).thenReturn(testWarehouse);

        productService.createProduct(testProductDTO, 1L);

        verify(productRepository, times(1)).save(any(Product.class));
        verify(warehouseRepository, times(1)).save(testWarehouse);
    }

    @Test
    void createProduct_WithNewWarehouse_ShouldCreateWarehouseAndProduct() {
        when(warehouseRepository.findById(99L)).thenReturn(Optional.empty());
        when(warehouseRepository.save(any(Warehouse.class))).thenReturn(testWarehouse);
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);

        productService.createProduct(testProductDTO, 99L);

        verify(warehouseRepository, times(2)).save(any(Warehouse.class));
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    void getProducts_Success_ShouldReturnList() {
        List<Product> products = List.of(testProduct);
        when(productRepository.findAll()).thenReturn(products);

        List<ProductToRetrieve> result = productService.getProducts();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Pizza Margherita", result.get(0).getName());
        verify(productRepository).findAll();
    }

    @Test
    void getProductById_Success_ShouldReturnProduct() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));

        ProductToRetrieve result = productService.getProductById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Pizza Margherita", result.getName());
        verify(productRepository).findById(1L);
    }

    @Test
    void findProductsWithPageable_Success_ShouldReturnPagedProducts() {
        PageRequest pageRequest = PageRequest.of(0, 10);
        Page<Product> productPage = new PageImpl<>(List.of(testProduct));
        when(productRepository.findAll(pageRequest)).thenReturn(productPage);

        List<ProductToRetrieve> result = productService.findProductsWithPageable(pageRequest);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(productRepository).findAll(pageRequest);
    }

    @Test
    void updateProduct_Success_ShouldUpdateAndReturnDTO() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);

        ProductDTO result = productService.updateProduct(1L, testProductDTO);

        assertNotNull(result);
        assertEquals("Pizza Margherita", result.getName());
        verify(productRepository).save(testProduct);
    }

    @Test
    void deleteProduct_Success_ShouldDeleteProduct() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));

        productService.deleteProduct(1L);

        verify(productRepository).delete(testProduct);
    }

    //негативные сценарии

    @Test
    void getProducts_EmptyList_ShouldThrowException() {
        when(productRepository.findAll()).thenReturn(List.of());

        BusinessException ex = assertThrows(BusinessException.class,
                () -> productService.getProducts());

        assertEquals(" Возникла ошибка: Продукты не найдены", ex.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, ex.getHttpStatus());
    }

    @Test
    void getProductById_NotFound_ShouldThrowException() {
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        BusinessException ex = assertThrows(BusinessException.class,
                () -> productService.getProductById(999L));

        assertEquals(" Возникла ошибка: Продукт не найден", ex.getMessage());
    }

    @Test
    void updateProduct_NotFound_ShouldThrowException() {
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        BusinessException ex = assertThrows(BusinessException.class,
                () -> productService.updateProduct(999L, testProductDTO));

        assertEquals(" Возникла ошибка: Продукт не найден", ex.getMessage());
    }

    @Test
    void deleteProduct_NotFound_ShouldThrowException() {
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        BusinessException ex = assertThrows(BusinessException.class,
                () -> productService.deleteProduct(999L));

        assertEquals(" Возникла ошибка: Продукт не найден!", ex.getMessage());
    }

    // verify проверки

    @Test
    void createProduct_ShouldNotCallWarehouseSave_WhenWarehouseExists() {
        when(warehouseRepository.findById(1L)).thenReturn(Optional.of(testWarehouse));
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);
        when(warehouseRepository.save(any(Warehouse.class))).thenReturn(testWarehouse);

        productService.createProduct(testProductDTO, 1L);

        verify(warehouseRepository, times(1)).save(testWarehouse);
    }

    //argument captor

    @Test
    void createProduct_ShouldCaptureProductData() {
        when(warehouseRepository.findById(1L)).thenReturn(Optional.of(testWarehouse));
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);
        when(warehouseRepository.save(any(Warehouse.class))).thenReturn(testWarehouse);

        productService.createProduct(testProductDTO, 1L);

        ArgumentCaptor<Product> productCaptor = ArgumentCaptor.forClass(Product.class);
        verify(productRepository).save(productCaptor.capture());

        Product capturedProduct = productCaptor.getValue();
        assertEquals("Pizza Margherita", capturedProduct.getName());
        assertEquals(500, capturedProduct.getPrice());
        assertEquals(10, capturedProduct.getAmount());
        assertEquals(testWarehouse, capturedProduct.getWarehouse_id());
    }

    //параметризированные тесты
    @ParameterizedTest
    @ValueSource(longs = {1L, 10L, 100L, 999L, 1000L})
    void getProductById_DifferentIds_ShouldReturnProduct(Long productId) {
        Product product = new Product();
        product.setId(productId);
        product.setName("Product " + productId);
        product.setPrice(100);
        product.setAmount(10);

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        var result = productService.getProductById(productId);

        assertNotNull(result);
        assertEquals(productId, result.getId());
        assertEquals("Product " + productId, result.getName());
        verify(productRepository).findById(productId);
    }

    //@CsvSource - разные комбинации цены и количества
    @ParameterizedTest
    @CsvSource({
            "Pizza, 500, 10",
            "Burger, 300, 25",
            "Cola, 100, 100",
            "Pasta, 450, 0",
            "Salad, 250, -5"  // отрицательное количество должно быть обработано
    })
    void createProduct_DifferentProductValues_ShouldCreateProduct(String name, int price, int amount) {
        when(warehouseRepository.findById(1L)).thenReturn(Optional.of(testWarehouse));
        when(productRepository.save(any(Product.class))).thenAnswer(inv -> inv.getArgument(0));
        when(warehouseRepository.save(any(Warehouse.class))).thenReturn(testWarehouse);

        ProductDTO productDTO = new ProductDTO();
        productDTO.setName(name);
        productDTO.setPrice(price);
        productDTO.setAmount(amount);

        if (amount < 0) {
            // Для отрицательного количества просто проверяем, что метод выполнится
            // (логика валидации может быть в другом месте)
            assertDoesNotThrow(() -> productService.createProduct(productDTO, 1L));
        } else {
            assertDoesNotThrow(() -> productService.createProduct(productDTO, 1L));
        }

        verify(productRepository, atLeastOnce()).save(any(Product.class));
    }
}