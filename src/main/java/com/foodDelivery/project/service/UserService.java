package com.foodDelivery.project.service;

import com.foodDelivery.project.domen.dto.UserDTO;
import com.foodDelivery.project.domen.model.User;
import com.foodDelivery.project.domen.model.enums.UserRole;
import com.foodDelivery.project.domen.responce.UserToRetrieve;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface UserService {

    List<UserToRetrieve> getUsers();

    void createUser(UserDTO userDTO);

    UserToRetrieve changeRole(Long id, UserRole role);

    UserDTO updateUser(Long id, UserDTO userDTO);

    void deleteUser(Long id);

    UserDetailsService userDetailsService();

    UserToRetrieve getUserById(Long id);
}
