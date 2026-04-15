package com.foodDelivery.project;

import com.foodDelivery.project.domen.dto.UserDTO;
import com.foodDelivery.project.domen.model.User;
import com.foodDelivery.project.domen.model.enums.UserRole;
import com.foodDelivery.project.domen.responce.UserToRetrieve;
import com.foodDelivery.project.repository.UserRepository;
import com.foodDelivery.project.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository repository;

    @InjectMocks
    private UserServiceImpl service;

    @Test
    void createUser_shouldSaveUser() {
        UserDTO dto = new UserDTO();
        dto.setUsername("john");
        dto.setRole("ROLE_USER");

        service.createUser(dto);

        verify(repository).save(any(User.class));
    }

    @Test
    void getUsers_shouldReturnList() {
        User user = new User();
        user.setUsername("john");
        user.setEmail("john@mail.com");

        when(repository.findAll()).thenReturn(List.of(user));

        List<UserToRetrieve> result = service.getUsers();

        assertEquals(1, result.size());
        assertEquals("john", result.get(0).getUserName());
    }

    @Test
    void updateUser_shouldModifyData() {
        User user = new User();
        user.setUsername("old");

        when(repository.findById(1L)).thenReturn(Optional.of(user));
        when(repository.save(any())).thenReturn(user);

        UserDTO dto = new UserDTO();
        dto.setUsername("new");

        UserDTO result = service.updateUser(1L, dto);

        assertEquals("new", result.getUsername());
    }

    @Test
    void deleteUser_shouldRemove() {
        User user = new User();

        when(repository.findById(1L)).thenReturn(Optional.of(user));

        service.deleteUser(1L);

        verify(repository).delete(user);
    }

    @Test
    void userDetailsService_shouldLoadUser() {
        User user = new User();
        user.setUsername("admin");
        user.setPassword("123");
        user.setRole(UserRole.ROLE_USER);

        when(repository.findUserByUsername("admin"))
                .thenReturn(Optional.of(user));

        var uds = service.userDetailsService();

        var result = uds.loadUserByUsername("admin");

        assertEquals("admin", result.getUsername());
    }
}
