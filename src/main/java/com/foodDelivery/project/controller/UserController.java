package com.foodDelivery.project.controller;

import com.foodDelivery.project.domen.dto.UserDTO;
import com.foodDelivery.project.domen.model.enums.UserRole;
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

    private final UserService service;

    @Autowired
    public UserController(UserService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Void> addUser(@RequestBody @Valid UserDTO userDTO){
        service.createUser(userDTO);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping
    public ResponseEntity<List<UserToRetrieve>> getUsers(){
        return ResponseEntity.ok(service.getUsers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserToRetrieve> getUserById(@PathVariable Long id){
        return ResponseEntity.ok(service.getUserById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> updateUser(
            @PathVariable Long id,
            @RequestBody @Valid UserDTO userDTO){

        return ResponseEntity.ok(service.updateUser(id, userDTO));
    }

    @PatchMapping("/{id}/role")
    public ResponseEntity<UserToRetrieve> changeUserRole(
            @PathVariable Long id,
            @RequestParam UserRole role){

        return ResponseEntity.ok(service.changeRole(id, role));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id){
        service.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
