package com.foodDelivery.project.controller;

import com.foodDelivery.project.domen.responce.OrderToRetrieve;
import com.foodDelivery.project.service.impl.OrderServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/admin")
public class AdminController {
}
