package com.foodDelivery.project.exception;

import com.foodDelivery.project.domen.dto.OrderDTO;
import com.foodDelivery.project.domen.dto.ProductAndAmount;
import com.foodDelivery.project.domen.model.*;
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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExceptionTest {

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
    private OrderDTO testOrderDTO;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("john_doe");
        testUser.setRole(UserRole.ROLE_USER);

        testOrderDTO = new OrderDTO();
        testOrderDTO.setTotalAmount(1000);
        testOrderDTO.setDeliveryFee(100);
        testOrderDTO.setStatus(OrderStatus.CREATED);
        testOrderDTO.setComment("Test comment");
        testOrderDTO.setPaymentMethod(PaymentMethod.CARD);
        testOrderDTO.setRating(5);
        testOrderDTO.setProductsId(List.of(new ProductAndAmount(10L, 2)));

        SecurityContextHolder.setContext(securityContext);
    }

    //Проверка типа исключения и сообщения при создании заказа без пользователя
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

    //Проверка типа исключения и сообщения при получении чужого заказа
    @Test
    void getOrderById_WhenOrderBelongsToAnotherUser_ShouldThrowBusinessExceptionWithForbiddenStatus() {
        User anotherUser = new User();
        anotherUser.setId(99L);
        anotherUser.setUsername("another_user");

        Order anotherOrder = new Order();
        anotherOrder.setId(100L);
        anotherOrder.setUser_id(anotherUser);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("john_doe");
        when(userRepository.findUserByUsername("john_doe")).thenReturn(Optional.of(testUser));
        when(orderRepository.findById(100L)).thenReturn(Optional.of(anotherOrder));

        BusinessException exception = assertThrows(BusinessException.class,
                () -> orderService.getOrderById(100L));

        assertEquals(" Возникла ошибка: Это не ваш заказ", exception.getMessage());
        assertEquals(HttpStatus.FORBIDDEN, exception.getHttpStatus());
    }

    //Проверка типа исключения и сообщения при нехватке товара на складе
    @Test
    void createOrder_WhenProductOutOfStock_ShouldThrowBusinessExceptionWithBadRequestStatus() {
        Product outOfStockProduct = new Product();
        outOfStockProduct.setId(10L);
        outOfStockProduct.setName("Pizza");
        outOfStockProduct.setPrice(500);
        outOfStockProduct.setAmount(1);

        User courier = new User();
        courier.setId(2L);
        courier.setRole(UserRole.ROLE_COURIER);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("john_doe");
        when(userRepository.findUserByUsername("john_doe")).thenReturn(Optional.of(testUser));
        when(userRepository.findUserByRole(UserRole.ROLE_COURIER)).thenReturn(Optional.of(courier));
        when(productRepository.findById(10L)).thenReturn(Optional.of(outOfStockProduct));

        BusinessException exception = assertThrows(BusinessException.class,
                () -> orderService.createOrder(testOrderDTO));

        assertEquals(" Возникла ошибка: Товар закончился на складе!", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());
    }
}