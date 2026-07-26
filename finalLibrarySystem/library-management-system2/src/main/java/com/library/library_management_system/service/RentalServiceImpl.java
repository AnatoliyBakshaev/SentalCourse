package com.library.library_management_system.service;

import com.library.library_management_system.entity.Book;
import com.library.library_management_system.entity.Rental;
import com.library.library_management_system.entity.User;
import com.library.library_management_system.repository.BookRepository;
import com.library.library_management_system.repository.RentalRepository;
import com.library.library_management_system.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class RentalServiceImpl implements RentalService {

    private static final Logger logger = LoggerFactory.getLogger(RentalServiceImpl.class);

    @Autowired
    private RentalRepository rentalRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional
    public Rental rentBook(Long userId, Long bookId, String notes) {
        logger.info("Попытка аренды книги: userId={}, bookId={}", userId, bookId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    logger.error("Пользователь не найден: ID={}", userId);
                    return new RuntimeException("Пользователь не найден");
                });

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> {
                    logger.error("Книга не найдена: ID={}", bookId);
                    return new RuntimeException("Книга не найдена");
                });

        if (book.getAvailableCopies() <= 0) {
            logger.warn("Книга недоступна для аренды: ID={}, availableCopies={}", bookId, book.getAvailableCopies());
            throw new RuntimeException("Книга недоступна для аренды");
        }

        // Проверка: есть ли уже активная аренда этой книги у пользователя
        List<Rental> activeRentals = rentalRepository.findActiveRentalsByUser(userId);
        for (Rental r : activeRentals) {
            if (r.getBook().getId().equals(bookId)) {
                logger.warn("Пользователь уже арендовал эту книгу: userId={}, bookId={}", userId, bookId);
                throw new RuntimeException("Вы уже арендовали эту книгу");
            }
        }

        // Уменьшаем количество доступных копий
        book.decrementAvailable();
        bookRepository.save(book);

        // Создаём аренду
        Rental rental = new Rental();
        rental.setUser(user);
        rental.setBook(book);
        rental.setNotes(notes);
        rental.setRentalDate(LocalDate.now());
        rental.setDueDate(LocalDate.now().plusDays(14));
        rental.setStatus("ACTIVE");
        rental.setActive(true);

        Rental savedRental = rentalRepository.save(rental);
        logger.info("Книга успешно арендована: rentalId={}, userId={}, bookId={}, dueDate={}",
                savedRental.getId(), userId, bookId, savedRental.getDueDate());
        return savedRental;
    }

    @Override
    @Transactional
    public Rental returnBook(Long rentalId) {
        logger.info("Попытка возврата книги: rentalId={}", rentalId);

        Rental rental = rentalRepository.findById(rentalId)
                .orElseThrow(() -> {
                    logger.error("Аренда не найдена: ID={}", rentalId);
                    return new RuntimeException("Аренда не найдена");
                });

        if (rental.isReturned()) {
            logger.warn("Книга уже возвращена: rentalId={}", rentalId);
            throw new RuntimeException("Книга уже возвращена");
        }

        Book book = rental.getBook();
        book.incrementAvailable();
        bookRepository.save(book);

        rental.setReturnDate(LocalDate.now());
        rental.setActive(false);
        rental.setStatus("RETURNED");

        if (rental.isOverdue()) {
            rental.setStatus("OVERDUE_RETURNED");
            logger.warn("Книга возвращена с просрочкой: rentalId={}, days overdue={}",
                    rentalId, java.time.temporal.ChronoUnit.DAYS.between(rental.getDueDate(), rental.getReturnDate()));
        }

        Rental savedRental = rentalRepository.save(rental);
        logger.info("Книга успешно возвращена: rentalId={}, bookId={}, returnDate={}",
                savedRental.getId(), savedRental.getBook().getId(), savedRental.getReturnDate());
        return savedRental;
    }

    @Override
    public List<Rental> getActiveRentalsByUser(Long userId) {
        logger.debug("Запрос активных аренд пользователя: userId={}", userId);
        return rentalRepository.findActiveRentalsByUser(userId);
    }

    @Override
    public List<Rental> getRentalHistoryByUser(Long userId) {
        logger.debug("Запрос истории аренд пользователя: userId={}", userId);
        return rentalRepository.findHistoryByUser(userId);
    }

    @Override
    public List<Rental> getAllRentals() {
        logger.info("Запрос списка всех аренд");
        return rentalRepository.findAll();
    }

    @Override
    public List<Rental> getOverdueRentals() {
        logger.info("Запрос просроченных аренд");
        return rentalRepository.findOverdueRentals(LocalDate.now());
    }

    @Override
    public List<Rental> getActiveRentals() {
        logger.info("Запрос всех активных аренд");
        return rentalRepository.findByIsActiveTrue();
    }

    @Override
    public Rental findById(Long id) {
        logger.debug("Поиск аренды по ID: {}", id);
        return rentalRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Аренда не найдена: ID={}", id);
                    return new RuntimeException("Аренда не найдена");
                });
    }

    @Override
    public long countActiveRentals() {
        logger.debug("Подсчёт активных аренд");
        return rentalRepository.countActiveRentals();
    }
}