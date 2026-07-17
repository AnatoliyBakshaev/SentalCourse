public interface IBookStore {
    void createOrder(int bookId, String customerName);
    void cancelOrder(int orderId);
    void changeOrderStatus(int orderId, OrderStatus status);
    void addBookToStock(int bookId, int quantity);
    void leaveRequest(int bookId, String customerName);
    void writeOffBook(int bookId);
}
