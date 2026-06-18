package com.foodDelivery.project.serviceTest;

import com.foodDelivery.project.domen.dto.UserDTO;
import com.foodDelivery.project.domen.model.User;
import com.foodDelivery.project.domen.model.enums.UserRole;
import com.foodDelivery.project.domen.responce.UserToRetrieve;
import com.foodDelivery.project.exception.BusinessException;
import com.foodDelivery.project.repository.UserRepository;
import com.foodDelivery.project.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContext securityContext;

    @InjectMocks
    private UserServiceImpl userService;

    private User testUser;
    private UserDTO testUserDTO;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("john_doe");
        testUser.setEmail("john@example.com");
        testUser.setPassword("encoded_password");
        testUser.setPhoneNumber("+123456789");
        testUser.setRole(UserRole.ROLE_USER);

        testUserDTO = new UserDTO();
        testUserDTO.setUsername("john_doe");
        testUserDTO.setEmail("john@example.com");
        testUserDTO.setPassword("password123");
        testUserDTO.setPhoneNumber("+123456789");
        testUserDTO.setRole("ROLE_USER");

        SecurityContextHolder.setContext(securityContext);
    }

    //позитивные сценарии

    @Test
    void changeRole_Success_ShouldChangeUserRole() {
        UserRole newRole = UserRole.ROLE_ADMIN;
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        UserToRetrieve result = userService.changeRole(1L, newRole);

        assertNotNull(result);
        assertEquals("john_doe", result.getUserName());
        verify(userRepository).save(testUser);
        assertEquals(UserRole.ROLE_ADMIN, testUser.getRole());
    }

    @Test
    void createUser_Success_ShouldCreateUser() {
        when(passwordEncoder.encode("password123")).thenReturn("encoded_password");
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        assertDoesNotThrow(() -> userService.createUser(testUserDTO));

        verify(userRepository).save(any(User.class));
        verify(passwordEncoder).encode("password123");
    }

    @Test
    void createUser_WithInvalidRole_ShouldSetDefaultRole() {
        testUserDTO.setRole("INVALID_ROLE");

        when(passwordEncoder.encode("password123")).thenReturn("encoded_password");
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        assertDoesNotThrow(() -> userService.createUser(testUserDTO));

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());

        assertEquals(UserRole.ROLE_USER, userCaptor.getValue().getRole());
    }

    @Test
    void getUsers_Success_ShouldReturnList() {
        when(userRepository.findAll()).thenReturn(List.of(testUser));

        List<UserToRetrieve> result = userService.getUsers();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("john_doe", result.get(0).getUserName());
        verify(userRepository).findAll();
    }

    @Test
    void getUsers_EmptyList_ShouldReturnDefaultList() {
        when(userRepository.findAll()).thenReturn(List.of());

        List<UserToRetrieve> result = userService.getUsers();

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals("user1", result.get(0).getUserName());
        assertEquals("email1", result.get(0).getEmail());
    }

    @Test
    void getUserById_Success_ShouldReturnUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        UserToRetrieve result = userService.getUserById(1L);

        assertNotNull(result);
        assertEquals("john_doe", result.getUserName());
        verify(userRepository).findById(1L);
    }

    @Test
    void updateUser_Success_ShouldUpdateAndReturnDTO() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.encode("password123")).thenReturn("encoded_password");
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        UserDTO result = userService.updateUser(1L, testUserDTO);

        assertNotNull(result);
        assertEquals("john_doe", result.getUsername());
        verify(userRepository).save(testUser);
    }

    @Test
    void updateCurrentUser_Success_ShouldUpdateCurrentUser() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("john_doe");
        when(userRepository.findUserByUsername("john_doe")).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        UserToRetrieve result = userService.updateCurrentUser(testUserDTO);

        assertNotNull(result);
        assertEquals("john_doe", result.getUserName());
        verify(userRepository).save(testUser);
    }

    @Test
    void updateCurrentUser_WithNullPassword_ShouldNotUpdatePassword() {
        testUserDTO.setPassword(null);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("john_doe");
        when(userRepository.findUserByUsername("john_doe")).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        userService.updateCurrentUser(testUserDTO);

        verify(passwordEncoder, never()).encode(any());
    }

    @Test
    void deleteUser_Success_ShouldDeleteUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        assertDoesNotThrow(() -> userService.deleteUser(1L));

        verify(userRepository).delete(testUser);
    }

    //негативные сценарии

    @Test
    void changeRole_UserNotFound_ShouldThrowException() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        BusinessException ex = assertThrows(BusinessException.class,
                () -> userService.changeRole(999L, UserRole.ROLE_ADMIN));

        assertEquals(" Возникла ошибка: Пользователь не найден", ex.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, ex.getHttpStatus());
    }

    @Test
    void getUserById_NotFound_ShouldThrowException() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        BusinessException ex = assertThrows(BusinessException.class,
                () -> userService.getUserById(999L));

        assertEquals(" Возникла ошибка: Пользователь не найден", ex.getMessage());
    }

    @Test
    void updateUser_NotFound_ShouldThrowException() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        BusinessException ex = assertThrows(BusinessException.class,
                () -> userService.updateUser(999L, testUserDTO));

        assertEquals(" Возникла ошибка: Пользователь не найден!", ex.getMessage());
    }

    @Test
    void updateCurrentUser_UserNotFound_ShouldThrowException() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("unknown_user");
        when(userRepository.findUserByUsername("unknown_user")).thenReturn(Optional.empty());

        BusinessException ex = assertThrows(BusinessException.class,
                () -> userService.updateCurrentUser(testUserDTO));

        assertEquals(" Возникла ошибка: Пользователь не найден", ex.getMessage());
    }

    @Test
    void deleteUser_NotFound_ShouldThrowException() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        BusinessException ex = assertThrows(BusinessException.class,
                () -> userService.deleteUser(999L));

        assertEquals(" Возникла ошибка: Пользователь не найден!", ex.getMessage());
    }

    //verify

    @Test
    void createUser_ShouldEncodePasswordBeforeSave() {
        when(passwordEncoder.encode("password123")).thenReturn("encoded_password");
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        userService.createUser(testUserDTO);

        verify(passwordEncoder).encode("password123");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void updateUser_WithInvalidRole_ShouldSetDefaultRole() {
        testUserDTO.setRole("INVALID");
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.encode("password123")).thenReturn("encoded_password");
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        userService.updateUser(1L, testUserDTO);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());

        assertEquals(UserRole.ROLE_USER, userCaptor.getValue().getRole());
    }

    //argument captor

    @Test
    void createUser_ShouldCaptureUserData() {
        when(passwordEncoder.encode("password123")).thenReturn("encoded_password");
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        userService.createUser(testUserDTO);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());

        User capturedUser = userCaptor.getValue();
        assertEquals("john_doe", capturedUser.getUsername());
        assertEquals("john@example.com", capturedUser.getEmail());
        assertEquals("encoded_password", capturedUser.getPassword());
        assertEquals("+123456789", capturedUser.getPhoneNumber());
    }

    @Test
    void updateCurrentUser_ShouldCaptureUpdatedUserData() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("john_doe");
        when(userRepository.findUserByUsername("john_doe")).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        userService.updateCurrentUser(testUserDTO);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());

        User capturedUser = userCaptor.getValue();
        assertEquals("john_doe", capturedUser.getUsername());
        assertEquals("john@example.com", capturedUser.getEmail());
    }
}