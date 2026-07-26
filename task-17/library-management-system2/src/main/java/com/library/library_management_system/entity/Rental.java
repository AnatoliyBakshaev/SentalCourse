package com.library.library_management_system.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;

@Entity
@Table(name = "rentals")
@Data
@NoArgsConstructor
@EqualsAndHashCode(exclude = {"user", "book"})  // ← ИСКЛЮЧАЕМ связи
@ToString(exclude = {"user", "book"})           // ← ИСКЛЮЧАЕМ связи
public class Rental {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @Column(name = "rental_date", nullable = false)
    private LocalDate rentalDate;

    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;

    @Column(name = "return_date")
    private LocalDate returnDate;

    @Column(name = "status", nullable = false, length = 20)
    private String status;

    @Column(name = "is_active", nullable = false)
    private boolean isActive = true;

    @Column(name = "notes", length = 500)
    private String notes;

    @PrePersist
    protected void onCreate() {
        rentalDate = LocalDate.now();
        dueDate = rentalDate.plusDays(14);
        status = "ACTIVE";
        isActive = true;
    }

    public boolean isOverdue() {
        return LocalDate.now().isAfter(dueDate) && returnDate == null;
    }

    public boolean isReturned() {
        return returnDate != null;
    }
}