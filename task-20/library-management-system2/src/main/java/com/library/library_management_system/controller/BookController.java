package com.library.library_management_system.controller;

import com.library.library_management_system.dto.BookDto;
import com.library.library_management_system.entity.Book;
import com.library.library_management_system.entity.Category;
import com.library.library_management_system.entity.User;
import com.library.library_management_system.service.BookService;
import com.library.library_management_system.service.CategoryService;
import com.library.library_management_system.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/books")
public class BookController {

    private static final Logger logger = LoggerFactory.getLogger(BookController.class);

    @Autowired
    private BookService bookService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private UserService userService;

    @GetMapping
    public String listBooks(@RequestParam(required = false) String search,
                            Model model,
                            HttpSession session) {
        User user = (User) session.getAttribute("loggedUser");
        if (user == null) {
            logger.warn("Неавторизованный доступ к /books");
            return "redirect:/login";
        }

        logger.info("Пользователь {} просматривает список книг, search={}", user.getUsername(), search);

        List<Book> books;
        if (search != null && !search.isEmpty()) {
            books = bookService.searchBooks(search);
            logger.info("Найдено {} книг по запросу '{}'", books.size(), search);
        } else {
            books = bookService.findAllBooks();
            logger.info("Загружено {} книг", books.size());
        }

        model.addAttribute("books", books);
        model.addAttribute("search", search);
        model.addAttribute("user", user);
        model.addAttribute("isAdmin", userService.isAdmin(user));
        return "books/list";
    }

    @GetMapping("/add")
    public String showAddForm(Model model, HttpSession session) {
        User user = (User) session.getAttribute("loggedUser");
        if (user == null || !userService.isAdmin(user)) {
            logger.warn("Неавторизованный доступ к /books/add, user={}", user != null ? user.getUsername() : "null");
            return "redirect:/login";
        }

        logger.info("Пользователь {} открывает форму добавления книги", user.getUsername());
        model.addAttribute("bookDto", new BookDto());
        model.addAttribute("categories", categoryService.findAllCategories());
        model.addAttribute("user", user);
        return "books/add";
    }

    @PostMapping("/add")
    public String addBook(@Valid @ModelAttribute BookDto bookDto,
                          BindingResult result,
                          Model model,
                          HttpSession session) {
        User user = (User) session.getAttribute("loggedUser");
        if (user == null || !userService.isAdmin(user)) {
            logger.warn("Неавторизованная попытка добавления книги");
            return "redirect:/login";
        }

        if (result.hasErrors()) {
            logger.warn("Ошибки валидации при добавлении книги: {}", result.getAllErrors());
            model.addAttribute("categories", categoryService.findAllCategories());
            model.addAttribute("user", user);
            return "books/add";
        }

        try {
            bookService.createBook(bookDto);
            logger.info("Книга успешно добавлена пользователем {}: title={}", user.getUsername(), bookDto.getTitle());
            return "redirect:/books";
        } catch (Exception e) {
            logger.error("Ошибка при добавлении книги: {}", e.getMessage(), e);
            model.addAttribute("error", e.getMessage());
            model.addAttribute("categories", categoryService.findAllCategories());
            model.addAttribute("user", user);
            return "books/add";
        }
    }

    @GetMapping("/{id}")
    public String viewBook(@PathVariable Long id, Model model, HttpSession session) {
        User user = (User) session.getAttribute("loggedUser");
        if (user == null) {
            logger.warn("Неавторизованный доступ к просмотру книги ID={}", id);
            return "redirect:/login";
        }

        logger.info("Пользователь {} просматривает книгу ID={}", user.getUsername(), id);

        try {
            Book book = bookService.findById(id)
                    .orElseThrow(() -> new RuntimeException("Книга не найдена"));

            model.addAttribute("book", book);
            model.addAttribute("user", user);
            model.addAttribute("isAdmin", userService.isAdmin(user));
            return "books/view";
        } catch (Exception e) {
            logger.error("Ошибка при просмотре книги ID={}: {}", id, e.getMessage(), e);
            return "redirect:/books";
        }
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model, HttpSession session) {
        User user = (User) session.getAttribute("loggedUser");
        if (user == null || !userService.isAdmin(user)) {
            logger.warn("Неавторизованный доступ к /books/edit/{}", id);
            return "redirect:/login";
        }

        logger.info("Пользователь {} открывает форму редактирования книги ID={}", user.getUsername(), id);

        try {
            Book book = bookService.findById(id)
                    .orElseThrow(() -> new RuntimeException("Книга не найдена"));

            BookDto bookDto = new BookDto();
            bookDto.setId(book.getId());
            bookDto.setTitle(book.getTitle());
            bookDto.setAuthor(book.getAuthor());
            bookDto.setIsbn(book.getIsbn());
            bookDto.setPublisher(book.getPublisher());
            bookDto.setPublicationYear(book.getPublicationYear());
            bookDto.setDescription(book.getDescription());
            bookDto.setTotalCopies(book.getTotalCopies());
            bookDto.setAvailableCopies(book.getAvailableCopies());
            bookDto.setCategoryIds(book.getCategories().stream()
                    .map(Category::getId)
                    .collect(Collectors.toSet()));

            model.addAttribute("bookDto", bookDto);
            model.addAttribute("categories", categoryService.findAllCategories());
            model.addAttribute("user", user);
            return "books/edit";
        } catch (Exception e) {
            logger.error("Ошибка при открытии формы редактирования книги ID={}: {}", id, e.getMessage(), e);
            return "redirect:/books";
        }
    }

    @PostMapping("/edit/{id}")
    public String updateBook(@PathVariable Long id,
                             @Valid @ModelAttribute BookDto bookDto,
                             BindingResult result,
                             Model model,
                             HttpSession session) {
        User user = (User) session.getAttribute("loggedUser");
        if (user == null || !userService.isAdmin(user)) {
            logger.warn("Неавторизованная попытка обновления книги ID={}", id);
            return "redirect:/login";
        }

        if (result.hasErrors()) {
            logger.warn("Ошибки валидации при обновлении книги ID={}: {}", id, result.getAllErrors());
            model.addAttribute("categories", categoryService.findAllCategories());
            model.addAttribute("user", user);
            return "books/edit";
        }

        try {
            bookService.updateBook(id, bookDto);
            logger.info("Книга ID={} успешно обновлена пользователем {}", id, user.getUsername());
            return "redirect:/books/" + id;
        } catch (Exception e) {
            logger.error("Ошибка при обновлении книги ID={}: {}", id, e.getMessage(), e);
            model.addAttribute("error", e.getMessage());
            model.addAttribute("categories", categoryService.findAllCategories());
            model.addAttribute("user", user);
            return "books/edit";
        }
    }

    @GetMapping("/delete/{id}")
    public String deleteBook(@PathVariable Long id, HttpSession session) {
        User user = (User) session.getAttribute("loggedUser");
        if (user == null || !userService.isAdmin(user)) {
            logger.warn("Неавторизованная попытка удаления книги ID={}", id);
            return "redirect:/login";
        }

        logger.warn("Пользователь {} удаляет книгу ID={}", user.getUsername(), id);

        try {
            bookService.deleteBook(id);
            logger.info("Книга ID={} успешно удалена", id);
            return "redirect:/books";
        } catch (Exception e) {
            logger.error("Ошибка при удалении книги ID={}: {}", id, e.getMessage(), e);
            return "redirect:/books";
        }
    }
}