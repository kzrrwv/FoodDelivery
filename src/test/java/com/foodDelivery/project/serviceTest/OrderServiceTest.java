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
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;

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
    private OrderDTO testOrderDTO;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("john_doe");
        testUser.setRole(UserRole.ROLE_USER);

        testCourier = new User();
        testCourier.setId(2L);
        testCourier.setRole(UserRole.ROLE_COURIER);

        testProduct = new Product();
        testProduct.setId(10L);
        testProduct.setName("Pizza");
        testProduct.setPrice(500);
        testProduct.setAmount(10);

        testOrder = new Order();
        testOrder.setId(100L);
        testOrder.setUser_id(testUser);
        testOrder.setStatus(OrderStatus.CREATED);
        testOrder.setTotalAmount(1000);

        testOrderDTO = new OrderDTO();
        testOrderDTO.setTotalAmount(1000);
        testOrderDTO.setDeliveryFee(100);
        testOrderDTO.setStatus(OrderStatus.CREATED);
        testOrderDTO.setComment("Fast delivery please");
        testOrderDTO.setPaymentMethod(PaymentMethod.CARD);
        testOrderDTO.setRating(5);
        testOrderDTO.setProductsId(List.of(
                new ProductAndAmount(10L, 2)
        ));
    }

    @BeforeEach
    void setupSecurityContext() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getName()).thenReturn("john_doe");
    }

    //1 тест исключения
    @Test
    void createOrder_WhenUserNotFound_ShouldThrowBusinessExceptionWithCorrectMessageAndStatus() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("unknown_user");
        when(userRepository.findUserByUsername("unknown_user")).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class,
                () -> orderService.createOrder(testOrderDTO));

        assertEquals(" Возникла ошибка: Пользователь не найден", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getHttpStatus());
    }
    //позитивные сценарии

    @Test
    void createOrder_Success_ShouldCreateOrderAndReview() {
        when(userRepository.findUserByUsername("john_doe")).thenReturn(Optional.of(testUser));
        when(userRepository.findUserByRole(UserRole.ROLE_COURIER)).thenReturn(Optional.of(testCourier));
        when(productRepository.findById(10L)).thenReturn(Optional.of(testProduct));
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);
        when(reviewService.createReviewWithOrder(any(ReviewDTO.class), any(Order.class)))
                .thenReturn(new Review());

        assertDoesNotThrow(() -> orderService.createOrder(testOrderDTO));

        verify(productRepository, times(1)).save(testProduct);
        verify(orderRepository, times(2)).save(any(Order.class));
        verify(reviewService).createReviewWithOrder(any(ReviewDTO.class), eq(testOrder));
        assertEquals(8, testProduct.getAmount()); // 10 - 2
    }

    @Test
    void getOrders_Success_ShouldReturnListOfOrders() {
        when(userRepository.findUserByUsername("john_doe")).thenReturn(Optional.of(testUser));
        when(orderRepository.findOrdersByUserId(1L)).thenReturn(List.of(testOrder));

        List<OrderToRetrieve> result = orderService.getOrders();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(100L, result.get(0).getId());
        verify(orderRepository).findOrdersByUserId(1L);
    }

    @Test
    void getOrderById_Success_ShouldReturnOrder() {
        when(userRepository.findUserByUsername("john_doe")).thenReturn(Optional.of(testUser));
        when(orderRepository.findById(100L)).thenReturn(Optional.of(testOrder));

        OrderToRetrieve result = orderService.getOrderById(100L);

        assertNotNull(result);
        assertEquals(100L, result.getId());
        verify(orderRepository).findById(100L);
    }

    @Test
    void updateOrder_Success_ShouldUpdateOrderAndReturnDTO() {
        when(userRepository.findUserByUsername("john_doe")).thenReturn(Optional.of(testUser));
        when(orderRepository.findById(100L)).thenReturn(Optional.of(testOrder));
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);

        OrderDTO updated = orderService.updateOrder(100L, testOrderDTO);

        assertNotNull(updated);
        verify(orderRepository).save(testOrder);
    }

    @Test
    void deleteOrder_Success_ShouldDeleteAndRestoreStock() {
        testOrder.setOrderItems(List.of());
        when(userRepository.findUserByUsername("john_doe")).thenReturn(Optional.of(testUser));
        when(orderRepository.findById(100L)).thenReturn(Optional.of(testOrder));

        assertDoesNotThrow(() -> orderService.deleteOrder(100L));

        verify(orderRepository).delete(testOrder);
    }

    //негативные сценарии

    @Test
    void createOrder_UserNotFound_ShouldThrowException() {
        when(userRepository.findUserByUsername("john_doe")).thenReturn(Optional.empty());

        BusinessException ex = assertThrows(BusinessException.class,
                () -> orderService.createOrder(testOrderDTO));

        assertEquals(" Возникла ошибка: Пользователь не найден", ex.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, ex.getHttpStatus());
        verify(orderRepository, never()).save(any());
    }

    @Test
    void createOrder_CourierNotFound_ShouldThrowException() {
        when(userRepository.findUserByUsername("john_doe")).thenReturn(Optional.of(testUser));
        when(userRepository.findUserByRole(UserRole.ROLE_COURIER)).thenReturn(Optional.empty());

        BusinessException ex = assertThrows(BusinessException.class,
                () -> orderService.createOrder(testOrderDTO));

        assertEquals(" Возникла ошибка: Курьер не найден!", ex.getMessage());
    }

    @Test
    void createOrder_ProductNotFound_ShouldThrowException() {
        when(userRepository.findUserByUsername("john_doe")).thenReturn(Optional.of(testUser));
        when(userRepository.findUserByRole(UserRole.ROLE_COURIER)).thenReturn(Optional.of(testCourier));
        when(productRepository.findById(10L)).thenReturn(Optional.empty());

        BusinessException ex = assertThrows(BusinessException.class,
                () -> orderService.createOrder(testOrderDTO));

        assertEquals(" Возникла ошибка: Продукт не найден!", ex.getMessage());
    }

    @Test
    void createOrder_NotEnoughStock_ShouldThrowException() {
        testProduct.setAmount(1);
        when(userRepository.findUserByUsername("john_doe")).thenReturn(Optional.of(testUser));
        when(userRepository.findUserByRole(UserRole.ROLE_COURIER)).thenReturn(Optional.of(testCourier));
        when(productRepository.findById(10L)).thenReturn(Optional.of(testProduct));

        BusinessException ex = assertThrows(BusinessException.class,
                () -> orderService.createOrder(testOrderDTO));

        assertEquals(" Возникла ошибка: Товар закончился на складе!", ex.getMessage());
    }

    @Test
    void getOrderById_OrderBelongsToAnotherUser_ShouldThrowException() {
        User anotherUser = new User();
        anotherUser.setId(99L);
        testOrder.setUser_id(anotherUser);

        when(userRepository.findUserByUsername("john_doe")).thenReturn(Optional.of(testUser));
        when(orderRepository.findById(100L)).thenReturn(Optional.of(testOrder));

        BusinessException ex = assertThrows(BusinessException.class,
                () -> orderService.getOrderById(100L));

        assertEquals(" Возникла ошибка: Это не ваш заказ", ex.getMessage());
        assertEquals(HttpStatus.FORBIDDEN, ex.getHttpStatus());
    }

    //verify
    @Test
    void createOrder_WhenStockSufficient_ShouldCallProductRepositorySave() {
        when(userRepository.findUserByUsername("john_doe")).thenReturn(Optional.of(testUser));
        when(userRepository.findUserByRole(UserRole.ROLE_COURIER)).thenReturn(Optional.of(testCourier));
        when(productRepository.findById(10L)).thenReturn(Optional.of(testProduct));
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);
        when(reviewService.createReviewWithOrder(any(), any())).thenReturn(new Review());

        orderService.createOrder(testOrderDTO);

        verify(productRepository, times(1)).save(testProduct);
        assertEquals(8, testProduct.getAmount());
    }

    @Test
    void updateOrder_WhenReviewExists_ShouldUpdateReviewFields() {
        Review existingReview = new Review();
        existingReview.setComment("Old comment");
        existingReview.setRating(3);
        testOrder.setReview_id(existingReview);

        when(userRepository.findUserByUsername("john_doe")).thenReturn(Optional.of(testUser));
        when(orderRepository.findById(100L)).thenReturn(Optional.of(testOrder));
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);

        orderService.updateOrder(100L, testOrderDTO);

        assertEquals("Fast delivery please", existingReview.getComment());
        assertEquals(5, existingReview.getRating());
    }

    //argument captor

    @Test
    void createOrder_ShouldPassCorrectProductsToOrderItems() {
        when(userRepository.findUserByUsername("john_doe")).thenReturn(Optional.of(testUser));
        when(userRepository.findUserByRole(UserRole.ROLE_COURIER)).thenReturn(Optional.of(testCourier));
        when(productRepository.findById(10L)).thenReturn(Optional.of(testProduct));
        when(orderRepository.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));
        when(reviewService.createReviewWithOrder(any(), any())).thenReturn(new Review());

        orderService.createOrder(testOrderDTO);

        ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);
        verify(orderRepository, atLeastOnce()).save(orderCaptor.capture());

        Order capturedOrder = orderCaptor.getValue();
        assertNotNull(capturedOrder.getOrderItems());
        assertEquals(1, capturedOrder.getOrderItems().size());
        assertEquals(2, capturedOrder.getOrderItems().get(0).getAmount());
        assertEquals(500, capturedOrder.getOrderItems().get(0).getPrice());
        assertEquals(testProduct, capturedOrder.getOrderItems().get(0).getProduct_id());
    }
}