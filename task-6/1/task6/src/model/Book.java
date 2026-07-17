package model;

import model.enums.BookStatus;
import java.time.LocalDate;

public class Book {
    private int id;
    private String title;
    private String author;
    private String isbn;
    private BookStatus status;
    private int quantity;
    private double price;
    private LocalDate publicationDate;
    private LocalDate receivedDate;
    private String description;
    private String genre;
    private String publisher;
    private int pages;


    // Конструктор для полной инициализации
    public Book(int id, String title, String author, String isbn, BookStatus status,
                double price, LocalDate publicationDate, String description,
                String genre, String publisher, int pages) {
        this.id = id;
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

    // Геттеры
    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public String getIsbn() { return isbn; }
    public BookStatus getStatus() { return status; }
    public int getQuantity() { return quantity; }
    public double getPrice() { return price; }
    public LocalDate getPublicationDate() { return publicationDate; }
    public LocalDate getReceivedDate() { return receivedDate; }
    public String getDescription() { return description; }
    public String getGenre() { return genre; }
    public String getPublisher() { return publisher; }
    public int getPages() { return pages; }

    // Сеттеры
    public void setId(int id) { this.id = id; }
    public void setTitle(String title) { this.title = title; }
    public void setAuthor(String author) { this.author = author; }
    public void setIsbn(String isbn) { this.isbn = isbn; }
    public void setStatus(BookStatus status) { this.status = status; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public void setPrice(double price) { this.price = price; }
    public void setPublicationDate(LocalDate publicationDate) { this.publicationDate = publicationDate; }
    public void setReceivedDate(LocalDate receivedDate) { this.receivedDate = receivedDate; }
    public void setDescription(String description) { this.description = description; }
    public void setGenre(String genre) { this.genre = genre; }
    public void setPublisher(String publisher) { this.publisher = publisher; }
    public void setPages(int pages) { this.pages = pages; }

    public void changeStatus(BookStatus newStatus) {
        this.status = newStatus;
        if (newStatus == BookStatus.IN_STOCK && quantity == 0) {
            quantity = 1;
            this.receivedDate = LocalDate.now();
        } else if (newStatus == BookStatus.WRITTEN_OFF) {
            quantity = 0;
        }
    }


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

    @Override
    public String toString() {
        return String.format("Книга #%d: '%s' %s (ISBN: %s) - %s, цена: %.2f руб., кол-во: %d",
                id, title, author, isbn, status.getDescription(), price, quantity);
    }
}
