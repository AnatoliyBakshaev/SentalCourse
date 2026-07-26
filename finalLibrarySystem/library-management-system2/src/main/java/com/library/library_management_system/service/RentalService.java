package com.library.library_management_system.service;

import com.library.library_management_system.entity.Rental;

import java.util.List;

public interface RentalService {

    Rental rentBook(Long userId, Long bookId, String notes);
    Rental returnBook(Long rentalId);
    List<Rental> getActiveRentalsByUser(Long userId);
    List<Rental> getRentalHistoryByUser(Long userId);
    List<Rental> getAllRentals();
    List<Rental> getOverdueRentals();
    List<Rental> getActiveRentals();
    Rental findById(Long id);
    long countActiveRentals();
}