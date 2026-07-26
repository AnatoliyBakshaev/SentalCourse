package com.library.library_management_system.controller;

import com.library.library_management_system.dto.UserRegistrationDto;
import com.library.library_management_system.entity.User;
import com.library.library_management_system.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private HttpSession session;

    @Mock
    private Model model;

    @Mock
    private BindingResult bindingResult;

    @InjectMocks
    private AuthController authController;

    private User testUser;
    private UserRegistrationDto registrationDto;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setPassword("test123");
        testUser.setFullName("Test User");

        registrationDto = new UserRegistrationDto();
        registrationDto.setUsername("newuser");
        registrationDto.setEmail("new@example.com");
        registrationDto.setPassword("new123");
        registrationDto.setFullName("New User");
    }


    @Test
    void showLoginForm_ShouldReturnLoginPage() {
        String result = authController.showLoginForm();
        assertEquals("login", result);
    }

    @Test
    void login_ShouldRedirectToDashboard_WhenCredentialsValid() {
        when(userService.authenticate("testuser", "test123")).thenReturn(testUser);

        String result = authController.login("testuser", "test123", session, model);

        assertEquals("redirect:/dashboard", result);
        verify(session, times(1)).setAttribute(eq("loggedUser"), any(User.class));
    }

    @Test
    void login_ShouldReturnLoginPage_WhenCredentialsInvalid() {
        when(userService.authenticate("testuser", "wrong")).thenReturn(null);

        String result = authController.login("testuser", "wrong", session, model);

        assertEquals("login", result);
        verify(session, never()).setAttribute(anyString(), any());
        verify(model, times(1)).addAttribute(eq("error"), anyString());
    }


    @Test
    void showRegisterForm_ShouldReturnRegisterPage() {
        String result = authController.showRegisterForm(model);

        assertEquals("register", result);
        verify(model, times(1)).addAttribute(eq("userDto"), any(UserRegistrationDto.class));
    }

    @Test
    void register_ShouldRedirectToLogin_WhenRegistrationSuccessful() {
        when(bindingResult.hasErrors()).thenReturn(false);
        when(userService.existsByUsername(anyString())).thenReturn(false);
        when(userService.existsByEmail(anyString())).thenReturn(false);
        when(userService.registerUser(any(UserRegistrationDto.class))).thenReturn(testUser);

        String result = authController.register(registrationDto, bindingResult, model);

        assertEquals("login", result);
        verify(userService, times(1)).registerUser(any(UserRegistrationDto.class));
    }

    @Test
    void register_ShouldReturnRegisterPage_WhenUsernameExists() {
        when(bindingResult.hasErrors()).thenReturn(true);
        when(userService.existsByUsername(anyString())).thenReturn(true);

        String result = authController.register(registrationDto, bindingResult, model);

        assertEquals("register", result);
        verify(userService, never()).registerUser(any(UserRegistrationDto.class));
    }

    @Test
    void register_ShouldReturnRegisterPage_WhenEmailExists() {
        when(bindingResult.hasErrors()).thenReturn(true);
        when(userService.existsByUsername(anyString())).thenReturn(false);
        when(userService.existsByEmail(anyString())).thenReturn(true);

        String result = authController.register(registrationDto, bindingResult, model);

        assertEquals("register", result);
        verify(userService, never()).registerUser(any(UserRegistrationDto.class));
    }

    @Test
    void register_ShouldReturnRegisterPage_WhenValidationErrors() {
        when(bindingResult.hasErrors()).thenReturn(true);

        String result = authController.register(registrationDto, bindingResult, model);

        assertEquals("register", result);
        verify(userService, never()).registerUser(any(UserRegistrationDto.class));
    }

    @Test
    void register_ShouldReturnRegisterPage_WhenExceptionThrown() {
        when(bindingResult.hasErrors()).thenReturn(false);
        when(userService.existsByUsername(anyString())).thenReturn(false);
        when(userService.existsByEmail(anyString())).thenReturn(false);
        when(userService.registerUser(any(UserRegistrationDto.class)))
                .thenThrow(new RuntimeException("Database error"));

        String result = authController.register(registrationDto, bindingResult, model);

        assertEquals("register", result);
        verify(model, times(1)).addAttribute(eq("error"), anyString());
    }


    @Test
    void dashboard_ShouldReturnDashboardPage_WhenUserLoggedIn() {
        when(session.getAttribute("loggedUser")).thenReturn(testUser);
        when(userService.isAdmin(any(User.class))).thenReturn(true);

        String result = authController.dashboard(session, model);

        assertEquals("dashboard", result);
        verify(model, times(1)).addAttribute(eq("user"), any(User.class));
    }

    @Test
    void dashboard_ShouldRedirectToLogin_WhenUserNotLoggedIn() {
        when(session.getAttribute("loggedUser")).thenReturn(null);

        String result = authController.dashboard(session, model);

        assertEquals("redirect:/login", result);
    }


    @Test
    void logout_ShouldInvalidateSessionAndRedirectToHome() {
        String result = authController.logout(session);

        assertEquals("redirect:/", result);
        verify(session, times(1)).invalidate();
    }
}