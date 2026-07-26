package com.library.library_management_system.controller;

import com.library.library_management_system.dto.RentalRequestDto;
import com.library.library_management_system.entity.Book;
import com.library.library_management_system.entity.Rental;
import com.library.library_management_system.entity.User;
import com.library.library_management_system.service.BookService;
import com.library.library_management_system.service.RentalService;
import com.library.library_management_system.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/rentals")
public class RentalController {

    @Autowired
    private RentalService rentalService;

    @Autowired
    private BookService bookService;

    @Autowired
    private UserService userService;

    @GetMapping("/my")
    public String myRentals(HttpSession session, Model model) {
        User user = (User) session.getAttribute("loggedUser");
        if (user == null) {
            return "redirect:/login";
        }

        List<Rental> activeRentals = rentalService.getActiveRentalsByUser(user.getId());
        List<Rental> history = rentalService.getRentalHistoryByUser(user.getId());

        model.addAttribute("activeRentals", activeRentals);
        model.addAttribute("history", history);
        model.addAttribute("user", user);
        model.addAttribute("isAdmin", userService.isAdmin(user.getId()));
        return "rentals/my";
    }

    @GetMapping("/all")
    public String allRentals(HttpSession session, Model model) {
        User user = (User) session.getAttribute("loggedUser");
        if (user == null || !userService.isAdmin(user.getId())) {
            return "redirect:/login";
        }

        List<Rental> allRentals = rentalService.getAllRentals();
        model.addAttribute("rentals", allRentals);
        model.addAttribute("user", user);
        model.addAttribute("isAdmin", true);
        return "rentals/all";
    }

    @GetMapping("/rent/{bookId}")
    public String showRentForm(@PathVariable Long bookId, Model model, HttpSession session) {
        User user = (User) session.getAttribute("loggedUser");
        if (user == null) {
            return "redirect:/login";
        }

        Book book = bookService.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Книга не найдена"));

        if (!book.isAvailable()) {
            model.addAttribute("error", "Книга暂时 недоступна");
            return "redirect:/books/" + bookId;
        }

        RentalRequestDto rentalDto = new RentalRequestDto();
        rentalDto.setBookId(bookId);

        model.addAttribute("rentalDto", rentalDto);
        model.addAttribute("book", book);
        model.addAttribute("user", user);
        return "rentals/rent";
    }

    @PostMapping("/rent")
    public String rentBook(@Valid @ModelAttribute RentalRequestDto rentalDto,
                           BindingResult result,
                           HttpSession session,
                           Model model) {
        User user = (User) session.getAttribute("loggedUser");
        if (user == null) {
            return "redirect:/login";
        }

        if (result.hasErrors()) {
            Book book = bookService.findById(rentalDto.getBookId())
                    .orElseThrow(() -> new RuntimeException("Книга не найдена"));
            model.addAttribute("book", book);
            model.addAttribute("user", user);
            return "rentals/rent";
        }

        try {
            rentalService.rentBook(user.getId(), rentalDto.getBookId(), rentalDto.getNotes());
            return "redirect:/rentals/my";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            Book book = bookService.findById(rentalDto.getBookId())
                    .orElseThrow(() -> new RuntimeException("Книга не найдена"));
            model.addAttribute("book", book);
            model.addAttribute("user", user);
            return "rentals/rent";
        }
    }

    @GetMapping("/return/{rentalId}")
    public String returnBook(@PathVariable Long rentalId, HttpSession session) {
        User user = (User) session.getAttribute("loggedUser");
        if (user == null) {
            return "redirect:/login";
        }

        try {
            rentalService.returnBook(rentalId);
            return "redirect:/rentals/my";
        } catch (Exception e) {
            return "redirect:/rentals/my?error=" + e.getMessage();
        }
    }

    @GetMapping("/overdue")
    public String overdueRentals(HttpSession session, Model model) {
        User user = (User) session.getAttribute("loggedUser");
        if (user == null || !userService.isAdmin(user.getId())) {
            return "redirect:/login";
        }

        List<Rental> overdue = rentalService.getOverdueRentals();
        model.addAttribute("overdue", overdue);
        model.addAttribute("user", user);
        model.addAttribute("isAdmin", true);
        return "rentals/overdue";
    }
}