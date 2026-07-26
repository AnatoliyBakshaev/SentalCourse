package com.library.library_management_system.controller;

import com.library.library_management_system.dto.UserRegistrationDto;
import com.library.library_management_system.entity.User;
import com.library.library_management_system.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private UserService userService;

    @GetMapping("/")
    public String home(HttpSession session) {
        if (session.getAttribute("loggedUser") != null) {
            logger.info("Пользователь уже авторизован, перенаправление на /dashboard");
            return "redirect:/dashboard";
        }
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String showLoginForm() {
        logger.debug("Открыта страница входа");
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String username,
                        @RequestParam String password,
                        HttpSession session,
                        Model model) {
        logger.info("Попытка входа: username={}", username);

        User user = userService.authenticate(username, password);

        if (user != null) {
            session.setAttribute("loggedUser", user);
            logger.info("Пользователь {} успешно вошёл в систему", username);
            return "redirect:/dashboard";
        } else {
            logger.warn("Неудачная попытка входа: username={}", username);
            model.addAttribute("error", "Неверный логин или пароль");
            return "login";
        }
    }

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        logger.debug("Открыта страница регистрации");
        model.addAttribute("userDto", new UserRegistrationDto());
        return "register";
    }

    @PostMapping("/register")
    public String register(@Valid UserRegistrationDto userDto,
                           BindingResult result,
                           Model model) {
        logger.info("Попытка регистрации: username={}", userDto.getUsername());

        if (userService.existsByUsername(userDto.getUsername())) {
            logger.warn("Регистрация отклонена: логин {} уже занят", userDto.getUsername());
            result.rejectValue("username", "error.userDto", "Логин уже занят");
        }
        if (userService.existsByEmail(userDto.getEmail())) {
            logger.warn("Регистрация отклонена: email {} уже зарегистрирован", userDto.getEmail());
            result.rejectValue("email", "error.userDto", "Email уже зарегистрирован");
        }

        if (result.hasErrors()) {
            logger.warn("Ошибки валидации при регистрации пользователя {}", userDto.getUsername());
            return "register";
        }

        try {
            userService.registerUser(userDto);
            logger.info("Пользователь {} успешно зарегистрирован", userDto.getUsername());
            model.addAttribute("success", "Регистрация успешна! Войдите в систему.");
            return "login";
        } catch (Exception e) {
            logger.error("Ошибка при регистрации пользователя {}: {}", userDto.getUsername(), e.getMessage(), e);
            model.addAttribute("error", "Ошибка регистрации: " + e.getMessage());
            return "register";
        }
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        User user = (User) session.getAttribute("loggedUser");
        if (user == null) {
            logger.warn("Неавторизованный доступ к /dashboard");
            return "redirect:/login";
        }

        boolean isAdmin = userService.isAdmin(user);
        logger.info("Пользователь {} открыл дашборд, isAdmin={}", user.getUsername(), isAdmin);

        model.addAttribute("user", user);
        model.addAttribute("isAdmin", isAdmin);
        return "dashboard";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        User user = (User) session.getAttribute("loggedUser");
        if (user != null) {
            logger.info("Пользователь {} вышел из системы", user.getUsername());
        }
        session.invalidate();
        return "redirect:/";
    }
}