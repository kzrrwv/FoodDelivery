package com.foodDelivery.project.serviceTest;

import com.foodDelivery.project.domen.dto.OrderDTO;
import com.foodDelivery.project.domen.dto.ProductAndAmount;
import com.foodDelivery.project.domen.dto.ReviewDTO;
import com.foodDelivery.project.domen.model.*;
import com.foodDelivery.project.domen.model.enums.OrderStatus;
import com.foodDelivery.project.domen.model.enums.PaymentMethod;
import com.foodDelivery.project.domen.model.enums.UserRole;
import com.foodDelivery.project.domen.responce.OrderToRetrieve;
import com.foodDelivery.project.exception.BusinessException;
import com.foodDelivery.project.repository.OrderRepository;
import com.foodDelivery.project.repository.ProductRepository;
import com.foodDelivery.project.repository.UserRepository;
import com.foodDelivery.project.service.impl.OrderServiceImpl;
import com.foodDelivery.project.service.impl.ReviewServiceImpl;
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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ReviewServiceImpl reviewService;

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContext securityContext;

    @InjectMocks
    private OrderServiceImpl orderService;

    private User testUser;
    private User testCourier;
    private Product testProduct;
    private Order testOrder;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setRole(UserRole.ROLE_USER);

        testCourier = new User();
        testCourier.setId(2L);
        testCourier.setUsername("courier");
        testCourier.setRole(UserRole.ROLE_COURIER);

        testProduct = new Product();
        testProduct.setId(1L);
        testProduct.setName("Pizza Margherita");
        testProduct.setPrice(500);
        testProduct.setAmount(100);

        testOrder = new Order();
        testOrder.setId(1L);
        testOrder.setUser_id(testUser);
        testOrder.setTotalAmount(1000);
        testOrder.setStatus(OrderStatus.CREATED);
        testOrder.setDeliveryFee(100);
        testOrder.setComment("Test comment");
        testOrder.setPaymentMethod(PaymentMethod.CARD);
        testOrder.setOrderItems(new ArrayList<>());

        // Mock SecurityContext
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getName()).thenReturn("testuser");
    }

    // ===== ПОЗИТИВНЫЕ СЦЕНАРИИ (4 теста) =====

    @Test
    void shouldCreateOrder_whenValidData() {
        // Arrange
        when(userRepository.findUserByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(userRepository.findUserByRole(UserRole.ROLE_COURIER)).thenReturn(Optional.of(testCourier));

        ProductAndAmount productAmount = new ProductAndAmount();
        productAmount.setId(1L);
        productAmount.setAmount(2);

        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setTotalAmount(1000);
        orderDTO.setDeliveryFee(100);
        orderDTO.setStatus(OrderStatus.CREATED);
        orderDTO.setComment("Please deliver quickly");
        orderDTO.setPaymentMethod(PaymentMethod.CARD);
        orderDTO.setProductsId(List.of(productAmount));
        orderDTO.setRating(5);

        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);

        Review mockReview = new Review();
        mockReview.setId(1L);
        when(reviewService.createReviewWithOrder(any(ReviewDTO.class), any(Order.class)))
                .thenReturn(mockReview);

        // Act
        orderService.createOrder(orderDTO);

        // Assert
        verify(orderRepository, atLeastOnce()).save(any(Order.class));
        verify(productRepository).save(testProduct);
        verify(reviewService).createReviewWithOrder(any(ReviewDTO.class), any(Order.class));
        assertThat(testProduct.getAmount()).isEqualTo(98);
    }

    @Test
    void shouldGetOrdersByCurrentUser() {
        // Arrange
        when(userRepository.findUserByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(orderRepository.findOrdersByUserId(1L)).thenReturn(List.of(testOrder));

        // Act
        List<OrderToRetrieve> result = orderService.getOrders();

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(1L);
        assertThat(result.get(0).getComment()).isEqualTo("Test comment");
        verify(orderRepository).findOrdersByUserId(1L);
    }

    @Test
    void shouldGetOrderById_whenUserOwnsOrder() {
        // Arrange
        when(userRepository.findUserByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));

        // Act
        OrderToRetrieve result = orderService.getOrderById(1L);

        // Assert
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getStatus()).isEqualTo(OrderStatus.CREATED.name());
        verify(orderRepository).findById(1L);
    }

    @Test
    void shouldUpdateOrder_whenUserOwnsOrder() {
        // Arrange
        when(userRepository.findUserByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);

        OrderDTO updateDTO = new OrderDTO();
        updateDTO.setTotalAmount(1500);
        updateDTO.setDeliveryFee(150);
        updateDTO.setComment("Updated comment");
        updateDTO.setRating(4);
        updateDTO.setStatus(OrderStatus.ON_THE_WAY);
        updateDTO.setDeliveredAt(LocalDateTime.now());

        // Act
        OrderDTO result = orderService.updateOrder(1L, updateDTO);

        // Assert
        verify(orderRepository).save(testOrder);
        assertThat(result.getTotalAmount()).isEqualTo(1500);
    }

    // ===== НЕГАТИВНЫЕ СЦЕНАРИИ (4 теста с исключениями) =====

    @Test
    void shouldThrowException_whenUserNotFound() {
        // Arrange
        when(userRepository.findUserByUsername("testuser")).thenReturn(Optional.empty());

        OrderDTO orderDTO = new OrderDTO();

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class,
                () -> orderService.createOrder(orderDTO));

        assertThat(exception.getMessage()).contains("Пользователь не найден");
        assertThat(exception.getHttpStatus()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void shouldThrowException_whenCourierNotFound() {
        // Arrange
        when(userRepository.findUserByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(userRepository.findUserByRole(UserRole.ROLE_COURIER)).thenReturn(Optional.empty());

        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setProductsId(new ArrayList<>());

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class,
                () -> orderService.createOrder(orderDTO));

        assertThat(exception.getMessage()).contains("Курьер не найден");
    }

    @Test
    void shouldThrowException_whenProductNotFound() {
        // Arrange
        when(userRepository.findUserByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(userRepository.findUserByRole(UserRole.ROLE_COURIER)).thenReturn(Optional.of(testCourier));

        ProductAndAmount productAmount = new ProductAndAmount();
        productAmount.setId(999L);
        productAmount.setAmount(1);

        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setProductsId(List.of(productAmount));
        orderDTO.setTotalAmount(100);

        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class,
                () -> orderService.createOrder(orderDTO));

        assertThat(exception.getMessage()).contains("Продукт не найден");
    }

    @Test
    void shouldThrowException_whenProductOutOfStock() {
        // Arrange
        when(userRepository.findUserByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(userRepository.findUserByRole(UserRole.ROLE_COURIER)).thenReturn(Optional.of(testCourier));

        testProduct.setAmount(0);

        ProductAndAmount productAmount = new ProductAndAmount();
        productAmount.setId(1L);
        productAmount.setAmount(1);

        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setProductsId(List.of(productAmount));
        orderDTO.setTotalAmount(100);

        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class,
                () -> orderService.createOrder(orderDTO));

        assertThat(exception.getMessage()).contains("Товар закончился на складе");
    }

    // ===== ПРОВЕРКА ВЗАИМОДЕЙСТВИЙ С VERIFY (3 теста) =====

    @Test
    void shouldCallRepositorySave_whenCreatingOrder() {
        // Arrange
        when(userRepository.findUserByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(userRepository.findUserByRole(UserRole.ROLE_COURIER)).thenReturn(Optional.of(testCourier));

        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setTotalAmount(500);
        orderDTO.setProductsId(new ArrayList<>());

        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);
        Review mockReview = new Review();
        when(reviewService.createReviewWithOrder(any(), any())).thenReturn(mockReview);

        // Act
        orderService.createOrder(orderDTO);

        // Assert
        verify(orderRepository, atLeast(1)).save(any(Order.class));
    }

    @Test
    void shouldNotCallRepositorySave_whenProductOutOfStock() {
        // Arrange
        when(userRepository.findUserByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(userRepository.findUserByRole(UserRole.ROLE_COURIER)).thenReturn(Optional.of(testCourier));

        testProduct.setAmount(0);

        ProductAndAmount productAmount = new ProductAndAmount();
        productAmount.setId(1L);
        productAmount.setAmount(1);

        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setProductsId(List.of(productAmount));

        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));

        // Act & Assert
        assertThrows(BusinessException.class, () -> orderService.createOrder(orderDTO));

        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void shouldCallDelete_whenDeletingOwnOrder() {
        // Arrange
        when(userRepository.findUserByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));
        testOrder.setOrderItems(new ArrayList<>());

        // Act
        orderService.deleteOrder(1L);

        // Assert
        verify(orderRepository).delete(testOrder);
    }

    // ===== ARGUMENT CAPTOR (1 тест) =====

    @Test
    void shouldPassCorrectDataToRepository_whenCreatingOrder() {
        // Arrange
        when(userRepository.findUserByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(userRepository.findUserByRole(UserRole.ROLE_COURIER)).thenReturn(Optional.of(testCourier));

        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setTotalAmount(2000);
        orderDTO.setDeliveryFee(150);
        orderDTO.setStatus(OrderStatus.CREATED);
        orderDTO.setComment("Leave at door");
        orderDTO.setPaymentMethod(PaymentMethod.CASH);
        orderDTO.setProductsId(new ArrayList<>());

        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);
        Review mockReview = new Review();
        when(reviewService.createReviewWithOrder(any(), any())).thenReturn(mockReview);

        ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);

        // Act
        orderService.createOrder(orderDTO);

        // Assert
        verify(orderRepository, atLeastOnce()).save(orderCaptor.capture());
        Order capturedOrder = orderCaptor.getValue();

        assertThat(capturedOrder.getTotalAmount()).isEqualTo(2000);
        assertThat(capturedOrder.getDeliveryFee()).isEqualTo(150);
        assertThat(capturedOrder.getComment()).isEqualTo("Leave at door");
        assertThat(capturedOrder.getPaymentMethod()).isEqualTo(PaymentMethod.CASH);
        assertThat(capturedOrder.getUser_id()).isEqualTo(testUser);
    }

    // ===== ПАРАМЕТРИЗОВАННЫЕ ТЕСТЫ (2 теста) =====

    @ParameterizedTest
    @CsvSource({
            "1, 100, 99",
            "5, 100, 95",
            "10, 50, 40",
            "0, 100, 100"
    })
    void shouldDecreaseProductAmountCorrectly(int amountToBuy, int initialAmount, int expectedRemaining) {
        // Arrange
        when(userRepository.findUserByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(userRepository.findUserByRole(UserRole.ROLE_COURIER)).thenReturn(Optional.of(testCourier));

        testProduct.setAmount(initialAmount);

        ProductAndAmount productAmount = new ProductAndAmount();
        productAmount.setId(1L);
        productAmount.setAmount(amountToBuy);

        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setProductsId(List.of(productAmount));
        orderDTO.setTotalAmount(1000);

        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);
        Review mockReview = new Review();
        when(reviewService.createReviewWithOrder(any(), any())).thenReturn(mockReview);

        // Act
        orderService.createOrder(orderDTO);

        // Assert
        assertThat(testProduct.getAmount()).isEqualTo(expectedRemaining);
    }

    @ParameterizedTest
    @ValueSource(longs = {1L, 5L, 10L, 100L})
    void shouldReturnOrder_whenOrderExists(Long orderId) {
        // Arrange
        when(userRepository.findUserByUsername("testuser")).thenReturn(Optional.of(testUser));

        Order order = new Order();
        order.setId(orderId);
        order.setUser_id(testUser);
        order.setStatus(OrderStatus.CREATED);
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        // Act
        OrderToRetrieve result = orderService.getOrderById(orderId);

        // Assert
        assertThat(result.getId()).isEqualTo(orderId);
    }
}