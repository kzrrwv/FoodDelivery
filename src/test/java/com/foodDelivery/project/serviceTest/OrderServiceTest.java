package com.foodDelivery.project.serviceTest;

import com.foodDelivery.project.domen.dto.OrderDTO;
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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository repository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ReviewServiceImpl reviewService;

    @InjectMocks
    private OrderServiceImpl service;

    private User user;

    @BeforeEach
    void setUp() {

        user = new User();
        user.setId(1L);
        user.setUsername("test");

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("test", null)
        );
    }

    @Test
    void createOrder_success() {

        when(userRepository.findUserByUsername("test"))
                .thenReturn(Optional.of(user));

        User courier = new User();
        courier.setRole(UserRole.ROLE_COURIER);

        when(userRepository.findUserByRole(UserRole.ROLE_COURIER))
                .thenReturn(Optional.of(courier));

        Product product = new Product();
        product.setId(1L);
        product.setAmount(5);

        when(productRepository.findById(1L))
                .thenReturn(Optional.of(product));

        OrderItem item = new OrderItem();
        item.setProduct_id(product);

        OrderDTO dto = new OrderDTO();
        dto.setTotalAmount(100);
        dto.setDeliveryFee(10);
        dto.setStatus(OrderStatus.ON_THE_WAY);
        dto.setPaymentMethod(PaymentMethod.CARD);
        dto.setRating(5);

        when(reviewService.createReviewWithOrder(any(), any()))
                .thenReturn(new Review());

        service.createOrder(dto);

        verify(repository).save(any(Order.class));
    }

    @Test
    void getOrderById_success() {

        Order order = new Order();
        order.setUser_id(user);

        when(repository.findById(1L))
                .thenReturn(Optional.of(order));

        when(userRepository.findUserByUsername("test"))
                .thenReturn(Optional.of(user));

        OrderToRetrieve result = service.getOrderById(1L);

        assertNotNull(result);
    }

    @Test
    void findOrdersWithPageable_success() {

        when(userRepository.findUserByUsername("test"))
                .thenReturn(Optional.of(user));

        Order order = new Order();

        when(repository.findOrdersByUserId(eq(1L), any(PageRequest.class)))
                .thenReturn(new PageImpl<>(List.of(order)));

        List<OrderToRetrieve> result =
                service.findOrdersWithPageable(PageRequest.of(0, 5));

        assertEquals(1, result.size());
    }

    @Test
    void updateOrder_success() {

        Review review = new Review();

        Order order = new Order();
        order.setUser_id(user);
        order.setReview_id(review);

        when(repository.findById(1L))
                .thenReturn(Optional.of(order));

        when(userRepository.findUserByUsername("test"))
                .thenReturn(Optional.of(user));

        when(repository.save(any(Order.class)))
                .thenReturn(order);

        OrderDTO dto = new OrderDTO();
        dto.setComment("updated");
        dto.setRating(5);

        OrderDTO result = service.updateOrder(1L, dto);

        assertNotNull(result);
    }

    @Test
    void deleteOrder_notFound() {

        when(repository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(BusinessException.class,
                () -> service.deleteOrder(1L));
    }
}