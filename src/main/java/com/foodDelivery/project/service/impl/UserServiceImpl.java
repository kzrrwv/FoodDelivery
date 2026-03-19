package com.foodDelivery.project.service.impl;

import com.foodDelivery.project.domen.dto.UserDTO;
import com.foodDelivery.project.domen.model.User;
import com.foodDelivery.project.domen.model.enums.UserRole;
import com.foodDelivery.project.domen.responce.UserToRetrieve;
import com.foodDelivery.project.exception.BusinessException;
import com.foodDelivery.project.repository.UserRepository;
import com.foodDelivery.project.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    private UserRepository repository;

    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    public UserServiceImpl(UserRepository repository) {
        this.repository = repository;
    }

    public void createUser(UserDTO userDTO){
        User user = new User();
        user.setUsername(userDTO.getUsername());
        user.setEmail(userDTO.getEmail());
        user.setPassword(userDTO.getPassword());
        user.setPhoneNumber(userDTO.getPhoneNumber());
        try{
            user.setRole(UserRole.valueOf(userDTO.getRole()));
        } catch(IllegalArgumentException e){
            log.debug("Неправильный формат роли, по умолчанию присваеваем customer.");
            user.setRole(UserRole.CUSTOMER);
        }
        repository.save(user);
    }

    public List<UserToRetrieve> getUsers(){
        List<User> all = repository.findAll();
        List<UserToRetrieve> userToRetrieves = new ArrayList<>();
        for(User user : all){
            UserToRetrieve userToRetrieve = new UserToRetrieve();
            userToRetrieve.setUserName(user.getUsername());
            userToRetrieve.setEmail(user.getEmail());
            userToRetrieves.add(userToRetrieve);
        }
        return userToRetrieves;
    }

    @Override
    public UserDTO updateUser(Long id, UserDTO userDTO) {
        return null;
    }

    @Override
    public void deleteUser(Long id) {
        User user = repository.findById(id)
                .orElseThrow(() -> new BusinessException("Пользователь не найден!", HttpStatus.NOT_FOUND));
        repository.delete(user);
        log.debug("Пользователь с id {} успешно удален.", id);
    }
}
