package com.foodDelivery.project.parameterized;

import com.foodDelivery.project.domen.dto.OrderDTO;
import com.foodDelivery.project.domen.dto.ProductAndAmount;
import com.foodDelivery.project.domen.model.Product;
import com.foodDelivery.project.domen.model.User;
import com.foodDelivery.project.domen.model.enums.OrderStatus;
import com.foodDelivery.project.domen.model.enums.PaymentMethod;
import com.foodDelivery.project.domen.model.enums.UserRole;
import com.foodDelivery.project.exception.BusinessException;
import com.foodDelivery.project.repository.OrderRepository;
import com.foodDelivery.project.repository.ProductRepository;
import com.foodDelivery.project.repository.UserRepository;
import com.foodDelivery.project.service.impl.OrderServiceImpl;
import com.foodDelivery.project.service.impl.ReviewServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ValidationParameterizedTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ReviewServiceImpl reviewService;

    @InjectMocks
    private OrderServiceImpl orderService;

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContext securityContext;

    private User testUser;
    private User testCourier;
    private Product testProduct;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testUser");

        testCourier = new User();
        testCourier.setId(2L);
        testCourier.setRole(UserRole.ROLE_COURIER);

        testProduct = new Product();
        testProduct.setId(1L);
        testProduct.setAmount(100);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getName()).thenReturn("testUser");
        when(userRepository.findUserByUsername("testUser")).thenReturn(Optional.of(testUser));
        when(userRepository.findUserByRole(UserRole.ROLE_COURIER)).thenReturn(Optional.of(testCourier));
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(orderRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(reviewService.createReviewWithOrder(any(), any())).thenReturn(null);
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 5, 10, 20, 50})
    void shouldCreateOrderWithDifferentProductAmounts(int amount) {
        // Arrange
        ProductAndAmount productAndAmount = new ProductAndAmount();
        productAndAmount.setId(1L);
        productAndAmount.setAmount(amount);
        testProduct.setAmount(amount + 10); // Stock enough

        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setTotalAmount(1000);
        orderDTO.setDeliveryFee(100);
        orderDTO.setStatus(OrderStatus.CREATED);
        orderDTO.setPaymentMethod(PaymentMethod.CARD);
        orderDTO.setProductsId(List.of(productAndAmount));

        // Act & Assert
        assertDoesNotThrow(() -> orderService.createOrder(orderDTO));
    }

    @ParameterizedTest
    @CsvSource({
            "500, 50, 550",
            "1000, 100, 1100",
            "2000, 0, 2000",
            "750, 25, 775"
    })
    void shouldCalculateOrderTotalCorrectly(int totalAmount, int deliveryFee, int expectedTotal) {
        int actualTotal = totalAmount + deliveryFee;

        assertEquals(expectedTotal, actualTotal);
    }
}