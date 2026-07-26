package com.library.library_management_system.controller;

import com.library.library_management_system.dto.UserProfileUpdateDto;
import com.library.library_management_system.entity.User;
import com.library.library_management_system.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/profile")
    public String showProfile(HttpSession session, Model model) {
        User user = (User) session.getAttribute("loggedUser");
        if (user == null) {
            return "redirect:/login";
        }

        model.addAttribute("user", user);
        model.addAttribute("profileDto", new UserProfileUpdateDto());
        return "profile";
    }

    @PostMapping("/profile/update")
    public String updateProfile(@Valid UserProfileUpdateDto profileDto,
                                BindingResult result,
                                HttpSession session,
                                Model model) {
        User user = (User) session.getAttribute("loggedUser");
        if (user == null) {
            return "redirect:/login";
        }

        if (result.hasErrors()) {
            model.addAttribute("user", user);
            return "profile";
        }

        try {
            userService.updateProfile(user.getId(), profileDto);
            session.setAttribute("loggedUser", userService.findById(user.getId()).get());
            model.addAttribute("success", "Профиль обновлён успешно!");
            return "profile";
        } catch (Exception e) {
            model.addAttribute("error", "Ошибка обновления: " + e.getMessage());
            model.addAttribute("user", user);
            return "profile";
        }
    }


}