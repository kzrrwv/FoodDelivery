package com.foodDelivery.project.service;

import com.foodDelivery.project.domen.dto.UserDTO;
import com.foodDelivery.project.domen.model.User;
import com.foodDelivery.project.domen.responce.UserToRetrieve;

import java.util.List;

public interface UserService {

    List<UserToRetrieve> getUsers();

    void saveUsers(UserDTO userDTO);
}
