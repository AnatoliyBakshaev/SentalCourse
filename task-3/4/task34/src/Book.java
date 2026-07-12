public class Book {
    private int id;
    private String title;
    private String author;
    private String isbn;
    private BookStatus status;
    private int quantity;

    public Book(int id, String title, String author, String isbn, BookStatus status) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.status = status;
        this.quantity = (status == BookStatus.IN_STOCK) ? 1 : 0;
    }

    // Геттеры и сеттеры
    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public String getIsbn() { return isbn; }
    public BookStatus getStatus() { return status; }
    public int getQuantity() { return quantity; }

    public void changeStatus(BookStatus newStatus) {
        this.status = newStatus;
        if (newStatus == BookStatus.IN_STOCK && quantity == 0) {
            quantity = 1;
        } else if (newStatus == BookStatus.WRITTEN_OFF) {
            quantity = 0;
        }
    }

    public void addQuantity(int amount) {
        if (amount > 0) {
            this.quantity += amount;
            if (this.quantity > 0 && this.status == BookStatus.OUT_OF_STOCK) {
                this.status = BookStatus.IN_STOCK;
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
        return String.format("Книга #%d: '%s' %s (ISBN: %s) - %s, кол-во: %d",
                id, title, author, isbn, status.getDescription(), quantity);
    }
}
