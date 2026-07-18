package bookstore.model;

import bookstore.model.enums.BookStatus;
import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "books")
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "title", nullable = false, length = 255)
    private String title;

    @Column(name = "author", nullable = false, length = 100)
    private String author;

    @Column(name = "isbn", unique = true, length = 20)
    private String isbn;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private BookStatus status;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    @Column(name = "price", nullable = false)
    private double price;

    @Column(name = "publication_date")
    private LocalDate publicationDate;

    @Column(name = "received_date")
    private LocalDate receivedDate;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "genre", length = 50)
    private String genre;

    @Column(name = "publisher", length = 100)
    private String publisher;

    @Column(name = "pages")
    private int pages;

    // ===== КОНСТРУКТОРЫ =====

    public Book() {}

    public Book(String title, String author, String isbn, BookStatus status,
                double price, LocalDate publicationDate, String description,
                String genre, String publisher, int pages) {
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.status = status;
        this.quantity = (status == BookStatus.IN_STOCK) ? 1 : 0;
        this.price = price;
        this.publicationDate = publicationDate;
        this.receivedDate = LocalDate.now();
        this.description = description;
        this.genre = genre;
        this.publisher = publisher;
        this.pages = pages;
    }

    public Book(Long id, String title, String author, String isbn, BookStatus status,
                double price, LocalDate publicationDate, String description,
                String genre, String publisher, int pages) {
        this(title, author, isbn, status, price, publicationDate, description,
                genre, publisher, pages);
        this.id = id;
    }

    // Для совместимости со старым кодом
    public Book(int id, String title, String author, String isbn, BookStatus status,
                double price, LocalDate publicationDate, String description,
                String genre, String publisher, int pages) {
        this((long) id, title, author, isbn, status, price, publicationDate,
                description, genre, publisher, pages);
    }

    // ===== ГЕТТЕРЫ =====

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }

    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }

    public BookStatus getStatus() { return status; }
    public void setStatus(BookStatus status) { this.status = status; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public LocalDate getPublicationDate() { return publicationDate; }
    public void setPublicationDate(LocalDate publicationDate) { this.publicationDate = publicationDate; }

    public LocalDate getReceivedDate() { return receivedDate; }
    public void setReceivedDate(LocalDate receivedDate) { this.receivedDate = receivedDate; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getGenre() { return genre; }
    public void setGenre(String genre) { this.genre = genre; }

    public String getPublisher() { return publisher; }
    public void setPublisher(String publisher) { this.publisher = publisher; }

    public int getPages() { return pages; }
    public void setPages(int pages) { this.pages = pages; }

    // ===== БИЗНЕС-МЕТОДЫ =====

    public void addQuantity(int amount) {
        if (amount > 0) {
            this.quantity += amount;
            if (this.quantity > 0 && this.status == BookStatus.OUT_OF_STOCK) {
                this.status = BookStatus.IN_STOCK;
                this.receivedDate = LocalDate.now();
            }
        }
    }

    public boolean reduceQuantity(int amount) {
        if (amount > 0 && this.quantity >= amount) {
            this.quantity -= amount;
            if (this.quantity == 0) {
                this.status = BookStatus.OUT_OF_STOCK;
            }
            return true;
        }
        return false;
    }

    public void changeStatus(BookStatus newStatus) {
        this.status = newStatus;
        if (newStatus == BookStatus.WRITTEN_OFF) {
            this.quantity = 0;
        }
    }

    @Override
    public String toString() {
        return String.format("Book{id=%d, title='%s', author='%s', status=%s, price=%.2f}",
                id, title, author, status, price);
    }
}