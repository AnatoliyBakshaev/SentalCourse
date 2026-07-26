package com.library.library_management_system.repository;

import com.library.library_management_system.entity.Rental;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface RentalRepository extends JpaRepository<Rental, Long> {

    List<Rental> findByUserId(Long userId);
    List<Rental> findByBookId(Long bookId);
    List<Rental> findByIsActiveTrue();
    List<Rental> findByIsActiveTrueAndUserId(Long userId);

    @Query("SELECT r FROM Rental r WHERE r.isActive = true AND r.dueDate < :date")
    List<Rental> findOverdueRentals(@Param("date") LocalDate date);

    @Query("SELECT r FROM Rental r WHERE r.user.id = :userId AND r.isActive = true")
    List<Rental> findActiveRentalsByUser(@Param("userId") Long userId);

    @Query("SELECT r FROM Rental r WHERE r.user.id = :userId AND r.isActive = false")
    List<Rental> findHistoryByUser(@Param("userId") Long userId);

    boolean existsByBookIdAndIsActiveTrue(Long bookId);

    @Query("SELECT COUNT(r) FROM Rental r WHERE r.isActive = true")
    long countActiveRentals();
}