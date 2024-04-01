package com.foodDelivery.project.serviceTest;

import com.foodDelivery.project.domen.dto.UserDTO;
import com.foodDelivery.project.domen.model.User;
import com.foodDelivery.project.domen.responce.UserToRetrieve;
import com.foodDelivery.project.exception.BusinessException;
import com.foodDelivery.project.repository.UserRepository;
import com.foodDelivery.project.service.impl.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository repository;

    @InjectMocks
    private UserServiceImpl service;

    @Test
    void createUser_success() {

        UserDTO dto = new UserDTO();
        dto.setUsername("test");
        dto.setRole("ROLE_USER");

        service.createUser(dto);

        verify(repository).save(any(User.class));
    }

    @Test
    void getUserById_success() {

        User user = new User();

        when(repository.findById(1L))
                .thenReturn(Optional.of(user));

        UserToRetrieve result = service.getUserById(1L);

        assertNotNull(result);
    }

    @Test
    void getUsers_success() {

        when(repository.findAll())
                .thenReturn(List.of(new User()));

        List<UserToRetrieve> result = service.getUsers();

        assertEquals(1, result.size());
    }

    @Test
    void updateUser_success() {

        User user = new User();

        when(repository.findById(1L))
                .thenReturn(Optional.of(user));

        when(repository.save(any(User.class)))
                .thenReturn(user);

        UserDTO dto = new UserDTO();
        dto.setRole("ROLE_ADMIN");

        UserDTO result = service.updateUser(1L, dto);

        assertNotNull(result);
    }

    @Test
    void deleteUser_notFound() {

        when(repository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(BusinessException.class,
                () -> service.deleteUser(1L));
    }
}