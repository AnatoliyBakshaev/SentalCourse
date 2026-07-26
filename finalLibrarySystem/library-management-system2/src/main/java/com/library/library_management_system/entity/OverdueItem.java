package com.library.library_management_system.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Entity
@Table(name = "overdue_items")
@Data
@NoArgsConstructor
public class OverdueItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rental_id", nullable = false)
    private Rental rental;

    @Column(name = "user_name", nullable = false, length = 100)
    private String userName;

    @Column(name = "book_title", nullable = false, length = 200)
    private String bookTitle;

    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;

    @Column(name = "days_overdue", nullable = false)
    private Long daysOverdue;

    @Column(name = "status", length = 20)
    private String status;

    @PrePersist
    protected void onCreate() {
        daysOverdue = java.time.temporal.ChronoUnit.DAYS.between(dueDate, LocalDate.now());
        status = "OVERDUE";
    }
}