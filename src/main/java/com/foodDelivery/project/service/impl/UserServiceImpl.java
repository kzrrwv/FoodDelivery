package com.foodDelivery.project.service.impl;

import com.foodDelivery.project.domen.dto.UserDTO;
import com.foodDelivery.project.domen.model.User;
import com.foodDelivery.project.domen.model.enums.UserRole;
import com.foodDelivery.project.domen.responce.UserToRetrieve;
import com.foodDelivery.project.exception.BusinessException;
import com.foodDelivery.project.repository.UserRepository;
import com.foodDelivery.project.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    private UserRepository repository;

    @Autowired
    public UserServiceImpl(UserRepository repository) {
        this.repository = repository;
    }

    //ошибка сериализация
    public List<UserToRetrieve> getUsers(){
        List<User> all = repository.findAll();

        if (all.isEmpty()) {
            throw new BusinessException(
                    "Пользователи не найдены",
                    HttpStatus.NOT_FOUND
            );
        }

        List<UserToRetrieve> userToRetrieves = new ArrayList<>();
        for(User user : all){
            UserToRetrieve userToRetrieve = new UserToRetrieve();
            userToRetrieve.setUserName(user.getUsername());
            userToRetrieve.setEmail(user.getEmail());
            userToRetrieves.add(userToRetrieve);
        }
        return userToRetrieves;
    }

    public void saveUsers(UserDTO userDTO){
        if (userDTO.getUsername() == null || userDTO.getUsername().isBlank()) {
            throw new BusinessException(
                    "Имя пользователя обязательно",
                    HttpStatus.BAD_REQUEST
            );
        }

        if (userDTO.getEmail() == null || userDTO.getEmail().isBlank()) {
            throw new BusinessException(
                    "Email обязателен",
                    HttpStatus.BAD_REQUEST
            );
        }

        if (userDTO.getPassword() == null || userDTO.getPassword().length() < 4) {
            throw new BusinessException(
                    "Пароль должен быть не менее 4 символов",
                    HttpStatus.BAD_REQUEST
            );
        }

        User user = new User();
        user.setUsername(userDTO.getUsername());
        user.setEmail(userDTO.getEmail());
        user.setPassword(userDTO.getPassword());
        user.setPhoneNumber(userDTO.getPhoneNumber());
        try{
            user.setRole(UserRole.valueOf(userDTO.getRole()));
        } catch(IllegalArgumentException e){
            user.setRole(UserRole.CUSTOMER);
        }
        repository.save(user);
    }
}
