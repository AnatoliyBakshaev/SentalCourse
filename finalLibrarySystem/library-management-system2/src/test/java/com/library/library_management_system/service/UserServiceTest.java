package com.library.library_management_system.service;

import com.library.library_management_system.dto.UserRegistrationDto;
import com.library.library_management_system.dto.UserProfileUpdateDto;
import com.library.library_management_system.entity.Role;
import com.library.library_management_system.entity.User;
import com.library.library_management_system.repository.RoleRepository;
import com.library.library_management_system.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private User testUser;
    private Role testRole;
    private UserRegistrationDto registrationDto;

    @BeforeEach
    void setUp() {
        testRole = new Role("USER");
        testRole.setId(1L);

        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setPassword("test123");
        testUser.setFullName("Test User");
        testUser.setEmail("test@example.com");
        testUser.setRoles(new HashSet<>(Set.of(testRole)));

        registrationDto = new UserRegistrationDto();
        registrationDto.setUsername("newuser");
        registrationDto.setPassword("new123");
        registrationDto.setFullName("New User");
        registrationDto.setEmail("new@example.com");
    }

    @Test
    void registerUser_ShouldReturnSavedUser() {
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(roleRepository.findByName("USER")).thenReturn(Optional.of(testRole));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        User result = userService.registerUser(registrationDto);

        assertNotNull(result);
        assertEquals(testUser.getUsername(), result.getUsername());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void registerUser_ShouldThrowException_WhenUsernameExists() {
        when(userRepository.existsByUsername(anyString())).thenReturn(true);

        assertThrows(RuntimeException.class, () -> userService.registerUser(registrationDto));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void authenticate_ShouldReturnUser_WhenCredentialsValid() {
        when(userRepository.findByUsernameWithRoles("testuser")).thenReturn(Optional.of(testUser));

        User result = userService.authenticate("testuser", "test123");

        assertNotNull(result);
        assertEquals(testUser.getUsername(), result.getUsername());
    }

    @Test
    void authenticate_ShouldReturnNull_WhenCredentialsInvalid() {
        when(userRepository.findByUsernameWithRoles("wronguser")).thenReturn(Optional.empty());

        User result = userService.authenticate("wronguser", "wrongpass");

        assertNull(result);
    }

    @Test
    void findById_ShouldReturnUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        Optional<User> result = userService.findById(1L);

        assertTrue(result.isPresent());
        assertEquals(testUser.getUsername(), result.get().getUsername());
    }

    @Test
    void updateProfile_ShouldUpdateUser() {
        UserProfileUpdateDto updateDto = new UserProfileUpdateDto();
        updateDto.setFullName("Updated Name");
        updateDto.setEmail("updated@example.com");
        updateDto.setPhone("+7-999-888-77-66");
        updateDto.setAddress("Moscow");

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        User result = userService.updateProfile(1L, updateDto);

        assertNotNull(result);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void isAdmin_ShouldReturnTrue_WhenUserHasAdminRole() {
        Role adminRole = new Role("ADMIN");
        adminRole.setId(2L);
        testUser.getRoles().add(adminRole);

        boolean result = userService.isAdmin(testUser);

        assertTrue(result);
    }

    @Test
    void isAdmin_ShouldReturnFalse_WhenUserDoesNotHaveAdminRole() {
        boolean result = userService.isAdmin(testUser);

        assertFalse(result);
    }
}