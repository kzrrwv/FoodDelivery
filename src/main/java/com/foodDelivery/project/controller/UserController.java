package com.foodDelivery.project.controller;

import com.foodDelivery.project.domen.dto.UserDTO;
import com.foodDelivery.project.domen.responce.UserToRetrieve;
import com.foodDelivery.project.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {
    private UserService service;

    @Autowired
    public UserController(UserService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<UserToRetrieve>> getUsers(){
        return ResponseEntity.ok(service.getUsers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable int id){
        return ResponseEntity.ok(new UserDTO());
    }

    @PostMapping
    public ResponseEntity<Void> addUser(@RequestBody @Valid UserDTO userDTO){
        service.saveUsers(userDTO);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
