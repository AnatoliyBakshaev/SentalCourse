package com.library.library_management_system.controller;

import com.library.library_management_system.entity.Category;
import com.library.library_management_system.entity.User;
import com.library.library_management_system.service.CategoryService;
import com.library.library_management_system.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/categories")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private UserService userService;

    @GetMapping
    public String listCategories(Model model, HttpSession session) {
        User user = (User) session.getAttribute("loggedUser");
        if (user == null) {
            return "redirect:/login";
        }

        List<Category> rootCategories = categoryService.findRootCategories();
        model.addAttribute("rootCategories", rootCategories);
        model.addAttribute("user", user);
        model.addAttribute("isAdmin", userService.isAdmin(user));
        return "categories/list";
    }

    @GetMapping("/add")
    public String showAddForm(Model model, HttpSession session) {
        User user = (User) session.getAttribute("loggedUser");
        if (user == null || !userService.isAdmin(user)) {
            return "redirect:/login";
        }

        model.addAttribute("category", new Category());
        model.addAttribute("allCategories", categoryService.findAllCategories());  // ← ИЗМЕНИТЬ НА ВСЕ
        model.addAttribute("user", user);
        return "categories/add";
    }
    @PostMapping("/add")
    public String addCategory(@RequestParam String name,
                              @RequestParam(required = false) String description,
                              @RequestParam(required = false) Long parentId,
                              HttpSession session,
                              Model model) {
        User user = (User) session.getAttribute("loggedUser");
        if (user == null || !userService.isAdmin(user.getId())) {
            return "redirect:/login";
        }

        try {
            categoryService.createCategory(name, description, parentId);
            return "redirect:/categories";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("category", new Category());
            model.addAttribute("parentCategories", categoryService.findRootCategories());
            model.addAttribute("user", user);
            return "categories/add";
        }
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model, HttpSession session) {
        User user = (User) session.getAttribute("loggedUser");
        if (user == null || !userService.isAdmin(user.getId())) {
            return "redirect:/login";
        }

        Category category = categoryService.findById(id)
                .orElseThrow(() -> new RuntimeException("Категория не найдена"));

        model.addAttribute("category", category);
        model.addAttribute("parentCategories", categoryService.findRootCategories());
        model.addAttribute("user", user);
        return "categories/edit";
    }

    @PostMapping("/edit/{id}")
    public String updateCategory(@PathVariable Long id,
                                 @RequestParam String name,
                                 @RequestParam(required = false) String description,
                                 @RequestParam(required = false) Long parentId,
                                 HttpSession session,
                                 Model model) {
        User user = (User) session.getAttribute("loggedUser");
        if (user == null || !userService.isAdmin(user.getId())) {
            return "redirect:/login";
        }

        try {
            categoryService.updateCategory(id, name, description, parentId);
            return "redirect:/categories";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("category", categoryService.findById(id).get());
            model.addAttribute("parentCategories", categoryService.findRootCategories());
            model.addAttribute("user", user);
            return "categories/edit";
        }
    }

    @GetMapping("/delete/{id}")
    public String deleteCategory(@PathVariable Long id, HttpSession session, Model model) {
        User user = (User) session.getAttribute("loggedUser");
        if (user == null || !userService.isAdmin(user.getId())) {
            return "redirect:/login";
        }

        try {
            categoryService.deleteCategory(id);
            return "redirect:/categories";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "categories/list";
        }
    }

    @GetMapping("/{id}")
    public String viewCategory(@PathVariable Long id, Model model, HttpSession session) {
        User user = (User) session.getAttribute("loggedUser");
        if (user == null) {
            return "redirect:/login";
        }

        Category category = categoryService.findById(id)
                .orElseThrow(() -> new RuntimeException("Категория не найдена"));

        List<Category> path = categoryService.getCategoryPath(id);

        model.addAttribute("category", category);
        model.addAttribute("path", path);
        model.addAttribute("books", category.getBooks());
        model.addAttribute("children", categoryService.findChildren(id));
        model.addAttribute("user", user);
        model.addAttribute("isAdmin", userService.isAdmin(user.getId()));
        return "categories/view";
    }
}