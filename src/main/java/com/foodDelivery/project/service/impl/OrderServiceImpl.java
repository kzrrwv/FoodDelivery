package com.foodDelivery.project.service.impl;

import com.foodDelivery.project.domen.dto.OrderDTO;
import com.foodDelivery.project.domen.dto.ProductAndAmount;
import com.foodDelivery.project.domen.dto.ReviewDTO;
import com.foodDelivery.project.domen.model.*;
import com.foodDelivery.project.domen.model.enums.OrderStatus;
import com.foodDelivery.project.domen.model.enums.UserRole;
import com.foodDelivery.project.domen.responce.OrderToRetrieve;
import com.foodDelivery.project.exception.BusinessException;
import com.foodDelivery.project.repository.OrderRepository;
import com.foodDelivery.project.repository.ProductRepository;
import com.foodDelivery.project.repository.UserRepository;
import com.foodDelivery.project.service.OrderService;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    private OrderRepository repository;

    private UserRepository userRepository;

    private ProductRepository productRepository;

    private ReviewServiceImpl reviewService;

    private static final Logger log = LoggerFactory.getLogger(OrderServiceImpl.class);

    @Autowired
    public OrderServiceImpl(OrderRepository repository, UserRepository userRepository, ReviewServiceImpl reviewService, ProductRepository productRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
        this.reviewService = reviewService;
        this.productRepository = productRepository;
    }

    private User getCurrentUser() {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        String username = authentication.getName();

        return userRepository.findUserByUsername(username)
                .orElseThrow(() ->
                        new BusinessException(
                                "Пользователь не найден",
                                HttpStatus.NOT_FOUND
                        ));
    }

    //доделать заполнение заказа продуктами из orderItems
    //при заказе уменьшать количество продуктов на складе
    //при создание нового заказа мы создаем новый orderItem, который берет доступные продукты со склада и убирает их со склада и добавляет их в orderItem
    //будем добавлять все orderItem в нулевой склад
    @Override
    @Transactional
    @PreAuthorize(value = "hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    public void createOrder(OrderDTO orderDTO){
        Order order = new Order();

        User currentUser = getCurrentUser();
        order.setUser_id(currentUser);

        order.setTotalAmount(orderDTO.getTotalAmount());
        order.setDeliveryFee(orderDTO.getDeliveryFee());
        order.setStatus(orderDTO.getStatus());
        order.setComment(orderDTO.getComment());
        order.setPaymentMethod(orderDTO.getPaymentMethod());

        User courier = userRepository.findUserByRole(UserRole.ROLE_COURIER)
                        .orElseThrow(() -> new BusinessException("Курьер не найден!", HttpStatus.NOT_FOUND));
        order.setCourier_id(courier);

        //!!
        List<OrderItem> items = new ArrayList<>();
        List<ProductAndAmount> productsId = orderDTO.getProductsId();
        for(ProductAndAmount product : productsId){

            Product dbProduct = productRepository.findById(product.getId())
                    .orElseThrow(() ->
                            new BusinessException(
                                    "Продукт не найден!",
                                    HttpStatus.NOT_FOUND));

            if (dbProduct.getAmount() < product.getAmount()) {
                throw new BusinessException(
                        "Товар закончился на складе!",
                        HttpStatus.BAD_REQUEST);
            }

            dbProduct.setAmount(dbProduct.getAmount() - product.getAmount());

            productRepository.save(dbProduct);

            OrderItem item = new OrderItem();

            item.setAmount(product.getAmount());
            item.setProduct_id(dbProduct);

            item.setOrder_id(order);
            item.setPrice(dbProduct.getPrice());
            items.add(item);
        }

        order.setOrderItems(items);
        Order savedOrder = repository.save(order);

        //из orderDTO сделать reviewDTO и передать в сервис
        ReviewDTO reviewDTO = new ReviewDTO();
        reviewDTO.setComment(orderDTO.getComment());
        reviewDTO.setRating(orderDTO.getRating());

        Review review = reviewService.createReviewWithOrder(reviewDTO, savedOrder);
        savedOrder.setReview_id(review);

        repository.save(savedOrder);
        log.info("Заказ успешно добавлен!");
    }

    @Override
    //получить список заказов по пользователю поменять
    //через userDetails, уже заригистрированный пользователь и мы смотрим есть ли у этого пользователя заказы
    public List<OrderToRetrieve> getOrders(){
        User currentUser = getCurrentUser();

        List<Order> orders =
                repository.findOrdersByUserId(
                        currentUser.getId()
                );

        List<OrderToRetrieve> result = new ArrayList<>();

        for(Order order : orders){
            OrderToRetrieve dto = new OrderToRetrieve();

            dto.setId(order.getId());
            dto.setComment(order.getComment());
            dto.setStatus(order.getStatus());
            dto.setDeliveryFee(order.getDeliveryFee());
            dto.setTotalAmount(order.getTotalAmount());

            result.add(dto);
        }
        return result;
    }

    @Override
    public OrderToRetrieve getOrderById(Long id) {
        Order order = repository.findById(id)
                .orElseThrow(() -> new BusinessException(
                        "Заказ не найден",
                        HttpStatus.NOT_FOUND
                ));
        User currentUser = getCurrentUser();

        if (!order.getUser_id().getId().equals(currentUser.getId())) {
            throw new BusinessException(
                    "Это не ваш заказ",
                    HttpStatus.FORBIDDEN
            );
        }

        OrderToRetrieve dto = new OrderToRetrieve();

        dto.setId(order.getId());
        dto.setComment(order.getComment());
        dto.setStatus(order.getStatus());
        dto.setDeliveryFee(order.getDeliveryFee());
        dto.setTotalAmount(order.getTotalAmount());

        log.info("Заказ с id {} успешно получен!", id);
        return dto;
    }

    @Override
    //переделать заказы по пользователю поиск
    public List<OrderToRetrieve> findOrdersWithPageable(PageRequest of) {
        User currentUser = getCurrentUser();

        Page<Order> page =
                repository.findOrdersByUserId(
                        currentUser.getId(),
                        of
                );

        List<OrderToRetrieve> result = new ArrayList<>();

        for (Order order : page.getContent()) {
            OrderToRetrieve dto = new OrderToRetrieve();

            dto.setId(order.getId());
            dto.setComment(order.getComment());
            dto.setStatus(order.getStatus());
            dto.setDeliveryFee(order.getDeliveryFee());
            dto.setTotalAmount(order.getTotalAmount());
            result.add(dto);
        }

        log.info("Список заказов успешно получен!");
        return result;
    }

    @Override
    @Transactional
    @PreAuthorize(value = "hasRole('ROLE_USER')")
    //сделать проверку является ли заказ пользователя
    //доделать с review
    public OrderDTO updateOrder(Long id, OrderDTO orderDTO) {
        Order order = repository.findById(id)
                .orElseThrow(() -> new BusinessException(
                        "Заказ не найден",
                        HttpStatus.NOT_FOUND
                ));

        User currentUser = getCurrentUser();

        if (!order.getUser_id().getId().equals(currentUser.getId())) {
            throw new BusinessException(
                    "Это не ваш заказ",
                    HttpStatus.FORBIDDEN
            );
        }

        order.setTotalAmount(orderDTO.getTotalAmount());
        order.setDeliveryFee(orderDTO.getDeliveryFee());
        order.setStatus(orderDTO.getStatus());
        order.setComment(orderDTO.getComment());
        order.setDeliveredAt(orderDTO.getDeliveredAt());

        Review review = order.getReview_id();

        if (review != null) {

            review.setComment(orderDTO.getComment());

            review.setRating(orderDTO.getRating());
        }

        Order saved = repository.save(order);

        OrderDTO dto = new OrderDTO();

        dto.setComment(saved.getComment());
        dto.setDeliveryFee(saved.getDeliveryFee());
        dto.setTotalAmount(saved.getTotalAmount());
        dto.setDeliveredAt(saved.getDeliveredAt());
        dto.setPaymentMethod(saved.getPaymentMethod());

        if (saved.getReview_id() != null) {
            dto.setRating(saved.getReview_id().getRating());
        }

        log.info("Заказ с id {} успешно изменён!", id);

        return dto;
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_COURIER')")
    public void updateOrderStatus(Long id, OrderStatus status, String comment) {
        Order order = repository.findById(id)
                .orElseThrow(() -> new BusinessException("Заказ не найден", HttpStatus.NOT_FOUND));

        order.setStatus(status);
        if (comment != null) {
            order.setComment(comment);
        }
        repository.save(order);

        log.info("Статус заказа {} изменен на {}", id, status);
    }

    //с заказом должны удаляться review, orderItem и восстанавливаться количество продутов
    @Override
    @PreAuthorize(value = "hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    public void deleteOrder(Long id) {
        Order order = repository.findById(id)
                .orElseThrow(() ->
                        new BusinessException(
                                "Заказ не найден",
                                HttpStatus.NOT_FOUND
                        ));

        User currentUser = getCurrentUser();

        if (!order.getUser_id().getId().equals(currentUser.getId())) {
            throw new BusinessException(
                    "Это не ваш заказ",
                    HttpStatus.FORBIDDEN
            );
        }

        // возвращаем товары на склад
        for (OrderItem item : order.getOrderItems()) {

            Product product = item.getProduct_id();

            product.setAmount(
                    product.getAmount() + item.getAmount()
            );

            productRepository.save(product);
        }

        // разрываем связь review -> order
        if (order.getReview_id() != null) {

            Review review = order.getReview_id();

            review.setOrder_id(null);

            order.setReview_id(null);
        }

        repository.delete(order);

        log.info("Заказ с id {} успешно удален.", id);
    }

    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public List<OrderToRetrieve> getAllOrdersForAdmin(PageRequest pageRequest) {
        Page<Order> page = repository.findAll(pageRequest);

        List<OrderToRetrieve> result = new ArrayList<>();

        for (Order order : page.getContent()) {
            OrderToRetrieve dto = new OrderToRetrieve();
            dto.setId(order.getId());
            dto.setComment(order.getComment());
            dto.setStatus(order.getStatus());
            dto.setDeliveryFee(order.getDeliveryFee());
            dto.setTotalAmount(order.getTotalAmount());
            result.add(dto);
        }

        log.info("Админ запросил все заказы. Найдено: {}", result.size());
        return result;
    }
}
