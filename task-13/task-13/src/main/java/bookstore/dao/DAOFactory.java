package bookstore.dao;

import bookstore.dao.impl.BookDAOJpaImpl;
import bookstore.dao.impl.OrderDAOJpaImpl;
import bookstore.dao.impl.RequestDAOJpaImpl;

public class DAOFactory {

    public static BookDAO getBookDAO() {
        return new BookDAOJpaImpl();
    }

    public static OrderDAO getOrderDAO() {
        return new OrderDAOJpaImpl();
    }

    public static RequestDAO getRequestDAO() {
        return new RequestDAOJpaImpl();
    }
}