package com.foodDelivery.project.serviceTest;

import com.foodDelivery.project.domen.dto.UserDTO;
import com.foodDelivery.project.domen.model.User;
import com.foodDelivery.project.domen.model.enums.UserRole;
import com.foodDelivery.project.exception.BusinessException;
import com.foodDelivery.project.repository.UserRepository;
import com.foodDelivery.project.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private User testUser;
    private UserDTO testUserDTO;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testUser");
        testUser.setEmail("test@example.com");
        testUser.setRole(UserRole.ROLE_USER);

        testUserDTO = new UserDTO();
        testUserDTO.setUsername("newUsername");
        testUserDTO.setEmail("new@example.com");
        testUserDTO.setRole("ROLE_ADMIN");
    }

    @Test
    void shouldChangeRole_whenUserExists() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        // Act
        var result = userService.changeRole(1L, UserRole.ROLE_ADMIN);

        // Assert
        assertThat(testUser.getRole()).isEqualTo(UserRole.ROLE_ADMIN);
        verify(userRepository, times(1)).save(testUser);
    }

    @Test
    void shouldThrowException_whenUserNotFoundOnRoleChange() {
        // Arrange
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            userService.changeRole(999L, UserRole.ROLE_ADMIN);
        });

        assertThat(exception.getMessage()).contains("Пользователь не найден");
        assertThat(exception.getHttpStatus()).isEqualTo(HttpStatus.NOT_FOUND);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void shouldThrowException_whenUserNotFoundOnGet() {
        // Arrange
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            userService.getUserById(999L);
        });

        assertThat(exception.getMessage()).contains("Пользователь не найден");
    }
}