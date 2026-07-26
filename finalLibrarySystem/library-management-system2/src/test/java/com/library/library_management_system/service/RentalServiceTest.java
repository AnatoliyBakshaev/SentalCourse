package com.library.library_management_system.service;

import com.library.library_management_system.entity.Book;
import com.library.library_management_system.entity.Rental;
import com.library.library_management_system.entity.User;
import com.library.library_management_system.repository.BookRepository;
import com.library.library_management_system.repository.RentalRepository;
import com.library.library_management_system.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RentalServiceTest {

    @Mock
    private RentalRepository rentalRepository;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private RentalServiceImpl rentalService;

    private User testUser;
    private Book testBook;
    private Rental testRental;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");

        testBook = new Book();
        testBook.setId(1L);
        testBook.setTitle("Test Book");
        testBook.setAvailableCopies(3);
        testBook.setTotalCopies(3);

        testRental = new Rental();
        testRental.setId(1L);
        testRental.setUser(testUser);
        testRental.setBook(testBook);
        testRental.setRentalDate(LocalDate.now());
        testRental.setDueDate(LocalDate.now().plusDays(14));
        testRental.setActive(true);
        testRental.setStatus("ACTIVE");
    }

    @Test
    void rentBook_ShouldCreateRental() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(bookRepository.findById(1L)).thenReturn(Optional.of(testBook));
        when(rentalRepository.findActiveRentalsByUser(1L)).thenReturn(List.of());
        when(rentalRepository.save(any(Rental.class))).thenReturn(testRental);

        Rental result = rentalService.rentBook(1L, 1L, "Test note");

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(bookRepository, times(1)).save(any(Book.class));
    }

    @Test
    void rentBook_ShouldThrowException_WhenNoCopiesAvailable() {
        testBook.setAvailableCopies(0);
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(bookRepository.findById(1L)).thenReturn(Optional.of(testBook));

        assertThrows(RuntimeException.class, () -> rentalService.rentBook(1L, 1L, null));
        verify(rentalRepository, never()).save(any(Rental.class));
    }

    @Test
    void returnBook_ShouldReturnBookAndUpdateRental() {
        testRental.setReturnDate(null);
        testBook.setAvailableCopies(2);

        when(rentalRepository.findById(1L)).thenReturn(Optional.of(testRental));
        when(rentalRepository.save(any(Rental.class))).thenReturn(testRental);

        Rental result = rentalService.returnBook(1L);

        assertNotNull(result);
        assertTrue(result.isReturned());
        verify(bookRepository, times(1)).save(any(Book.class));
    }

    @Test
    void returnBook_ShouldThrowException_WhenAlreadyReturned() {
        testRental.setReturnDate(LocalDate.now());

        when(rentalRepository.findById(1L)).thenReturn(Optional.of(testRental));

        assertThrows(RuntimeException.class, () -> rentalService.returnBook(1L));
    }

    @Test
    void getActiveRentalsByUser_ShouldReturnActiveRentals() {
        when(rentalRepository.findActiveRentalsByUser(1L)).thenReturn(List.of(testRental));

        List<Rental> result = rentalService.getActiveRentalsByUser(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.get(0).isActive());
    }

    @Test
    void getOverdueRentals_ShouldReturnOverdueRentals() {
        when(rentalRepository.findOverdueRentals(any(LocalDate.class))).thenReturn(List.of(testRental));

        List<Rental> result = rentalService.getOverdueRentals();

        assertNotNull(result);
        assertEquals(1, result.size());
    }
}