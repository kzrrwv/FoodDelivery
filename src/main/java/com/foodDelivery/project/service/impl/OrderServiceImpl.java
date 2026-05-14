package com.foodDelivery.project.service.impl;

import com.foodDelivery.project.domen.dto.OrderDTO;
import com.foodDelivery.project.domen.dto.ReviewDTO;
import com.foodDelivery.project.domen.model.Order;
import com.foodDelivery.project.domen.model.OrderItem;
import com.foodDelivery.project.domen.model.Product;
import com.foodDelivery.project.domen.model.User;
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

    //доделать заполнение заказа продуктами из orderItems
    //при заказе уменьшать количество продуктов на складе
    @Override
    @Transactional
    @PreAuthorize(value = "hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    public void createOrder(OrderDTO orderDTO){
        Order order = new Order();
//        repository.save(order);

        order.setTotalAmount(orderDTO.getTotalAmount());
        order.setDeliveryFee(orderDTO.getDeliveryFee());
        order.setStatus(orderDTO.getStatus());
        order.setComment(orderDTO.getComment());
        order.setPaymentMethod(orderDTO.getPaymentMethod());

        User courier = userRepository.findUserByRole(UserRole.ROLE_COURIER)
                        .orElseThrow(() -> new BusinessException("Курьер не найден!", HttpStatus.NOT_FOUND));
        order.setCourier_id(courier);

        List<OrderItem> items = new ArrayList<>();

        for (OrderItem item : orderDTO.getOrderItems()) {

            Product product = item.getProduct_id();

            if (product == null) {
                throw new BusinessException(
                        "Продукт отсутствует!",
                        HttpStatus.BAD_REQUEST);
            }

            Product dbProduct = productRepository.findById(product.getId())
                    .orElseThrow(() ->
                            new BusinessException(
                                    "Продукт не найден!",
                                    HttpStatus.NOT_FOUND));

            if (dbProduct.getAmount() <= 0) {
                throw new BusinessException(
                        "Товар закончился на складе!",
                        HttpStatus.BAD_REQUEST);
            }

            dbProduct.setAmount(dbProduct.getAmount() - 1);

            item.setProduct_id(dbProduct);

            item.setOrder_id(order);

            items.add(item);
        }

        order.setOrderItems(items);

        //из orderDTO сделать reviewDTO и передать в сервис
        ReviewDTO reviewDTO = new ReviewDTO();
        reviewDTO.setComment(orderDTO.getComment());
        reviewDTO.setRating(orderDTO.getRating());

        order.setReview_id(reviewService.createReviewWithOrder(reviewDTO, order));
        repository.save(order);
        log.info("Заказ успешно добавлен!");
    }

    @Override
    //получить список заказов по пользователю поменять
    //через userDetails, уже заригистрированный пользователь и мы смотрим есть ли у этого пользователя заказы
    public List<OrderToRetrieve> getOrders(){
        List<Order> all = repository.findAll();
        List<OrderToRetrieve> orderToRetrieves = new ArrayList<>();

        for(Order order : all){
            OrderToRetrieve orderToRetrieve = new OrderToRetrieve();
            orderToRetrieve.setComment(order.getComment());
            orderToRetrieve.setStatus(order.getStatus());
            orderToRetrieve.setDeliveryFee(order.getDeliveryFee());
            orderToRetrieve.setTotalAmount(order.getTotalAmount());
            orderToRetrieves.add(orderToRetrieve);
        }
        return orderToRetrieves;
    }

    @Override
    public OrderToRetrieve getOrderById(Long id) {
        Order order = repository.findById(id)
                .orElseThrow(() -> new BusinessException(
                        "Заказ не найден",
                        HttpStatus.NOT_FOUND
                ));

        OrderToRetrieve dto = new OrderToRetrieve();
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
        Page<Order> page = repository.findAll(of);

        List<OrderToRetrieve> result = new ArrayList<>();

        for (Order order : page.getContent()) {
            OrderToRetrieve dto = new OrderToRetrieve();
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
    public OrderDTO updateOrder(Long id, OrderDTO orderDTO, ReviewDTO reviewDTO) {
        Order order = repository.findById(id)
                .orElseThrow(() -> new BusinessException(
                        "Заказ не найден",
                        HttpStatus.NOT_FOUND
                ));

        order.setTotalAmount(orderDTO.getTotalAmount());
        order.setDeliveryFee(orderDTO.getDeliveryFee());
        order.setStatus(orderDTO.getStatus());
        order.setComment(orderDTO.getComment());
        order.setDeliveredAt(orderDTO.getDeliveredAt());

        Order saved = repository.save(order);



        log.info("Заказ с id {} успешно изменён!", id);
        return null;
    }

    @Override
    @PreAuthorize(value = "hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    public void deleteOrder(Long id) {
        Order order = repository.findById(id)
                .orElseThrow(() -> new BusinessException("Заказ не найден", HttpStatus.NOT_FOUND));

        repository.delete(order);

        log.info("Заказ с id {} успешно удален.", id);
    }
}
