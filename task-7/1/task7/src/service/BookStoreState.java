package service;

import model.Book;
import model.Order;
import model.Request;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class BookStoreState implements Serializable {
    private static final long serialVersionUID = 1L;

    private Map<Integer, Book> books;
    private Map<Integer, Order> orders;
    private List<Request> requests;
    private int orderCounter;
    private double totalRevenue;

    public Map<Integer, Book> getBooks() {
        return books;
    }

    public void setBooks(Map<Integer, Book> books) {
        this.books = books;
    }

    public Map<Integer, Order> getOrders() {
        return orders;
    }

    public void setOrders(Map<Integer, Order> orders) {
        this.orders = orders;
    }

    public List<Request> getRequests() {
        return requests;
    }

    public void setRequests(List<Request> requests) {
        this.requests = requests;
    }

    public int getOrderCounter() {
        return orderCounter;
    }

    public void setOrderCounter(int orderCounter) {
        this.orderCounter = orderCounter;
    }

    public double getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(double totalRevenue) {
        this.totalRevenue = totalRevenue;
    }
}
