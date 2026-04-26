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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    private UserRepository repository;

    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    public UserServiceImpl(UserRepository repository) {
        this.repository = repository;
    }

    public UserDetailsService userDetailsService(){
        return username -> {
            User user = repository.findUserByUsername(username).get();
                    return new org.springframework.security.core.userdetails.User(
                            user.getUsername(),
                            user.getPassword(),
                            List.of(new SimpleGrantedAuthority(user.getRole().name()))
                    );
        };
    }

    @Override
//    @PreAuthorize(value = "hasRole('ROLE_ADMIN')")
    public UserToRetrieve changeRole(Long id, UserRole role){
        User user = repository.findById(id)
                .orElseThrow(() -> new BusinessException(
                        "Пользователь не найден",
                        HttpStatus.NOT_FOUND
                ));

        user.setRole(role);

        User saved = repository.save(user);

        log.info("Роль пользователя с id {} изменена на {}", id, role);

        UserToRetrieve dto = new UserToRetrieve();
        dto.setUserName(saved.getUsername());
        dto.setEmail(saved.getEmail());

        return dto;
    }

//    @PreAuthorize(value = "hasRole('ROLE_ADMIN')")
    public void createUser(UserDTO userDTO){
        User user = new User();
        user.setUsername(userDTO.getUsername());
        user.setEmail(userDTO.getEmail());
        user.setPassword(userDTO.getPassword());
        user.setPhoneNumber(userDTO.getPhoneNumber());
        try{
            user.setRole(UserRole.valueOf(userDTO.getRole()));
        } catch(IllegalArgumentException e){
            log.debug("Неправильный формат роли, по умолчанию присваеваем user.");
            user.setRole(UserRole.ROLE_USER);
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
        if(userToRetrieves.isEmpty()){
            return new ArrayList<>(Arrays.asList(new UserToRetrieve("user1", "email1")));
        }
        return userToRetrieves;
    }

    @Override
//    @PreAuthorize(value = "hasRole('ROLE_ADMIN')")
    public UserToRetrieve getUserById(Long id) {
        User user = repository.findById(id)
                .orElseThrow(() -> new BusinessException(
                        "Пользователь не найден",
                        HttpStatus.NOT_FOUND
                ));

        UserToRetrieve dto = new UserToRetrieve();
        dto.setUserName(user.getUsername());
        dto.setEmail(user.getEmail());

        return dto;
    }

    @Override
//    @PreAuthorize(value = "hasRole('ROLE_ADMIN')")
    public UserDTO updateUser(Long id, UserDTO userDTO) {
        User user = repository.findById(id)
                .orElseThrow(() -> new BusinessException("Пользователь не найден!", HttpStatus.NOT_FOUND));

        user.setUsername(userDTO.getUsername());
        user.setEmail(userDTO.getEmail());
        user.setPassword(userDTO.getPassword());
        user.setPhoneNumber(userDTO.getPhoneNumber());
        try{
            user.setRole(UserRole.valueOf(userDTO.getRole()));
        } catch(IllegalArgumentException e){
            log.debug("Неправильный формат роли!");
            user.setRole(UserRole.ROLE_USER);
        }

        User saved = repository.save(user);
        UserDTO dto = new UserDTO();
        dto.setEmail(saved.getEmail());
        dto.setUsername(saved.getUsername());
        dto.setPassword(saved.getPassword());
        dto.setPhoneNumber(saved.getPhoneNumber());
        dto.setRole(saved.getRole().name());

        return dto;
    }

    @Override
//    @PreAuthorize(value = "hasRole('ROLE_ADMIN')")
    public void deleteUser(Long id) {
        User user = repository.findById(id)
                .orElseThrow(() -> new BusinessException("Пользователь не найден!", HttpStatus.NOT_FOUND));
        repository.delete(user);
        log.debug("Пользователь с id {} успешно удален.", id);
    }
}
