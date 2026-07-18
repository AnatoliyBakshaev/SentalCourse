package bookstore.dao.impl;

import bookstore.dao.OrderDAO;
import bookstore.model.Order;
import bookstore.model.enums.OrderStatus;
import bookstore.utils.JpaUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import java.time.LocalDateTime;
import java.util.List;

public class OrderDAOJpaImpl implements OrderDAO {

    @Override
    public Order getById(Long id) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            return em.find(Order.class, id);
        } finally {
            em.close();
        }
    }

    @Override
    public List<Order> getAll() {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            TypedQuery<Order> query = em.createQuery(
                    "SELECT o FROM Order o ORDER BY o.id", Order.class);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public List<Order> findAll() {
        return getAll();
    }

    @Override
    public boolean save(Order order) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(order);
            em.getTransaction().commit();
            return true;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Ошибка сохранения заказа", e);
        } finally {
            em.close();
        }
    }

    @Override
    public boolean update(Order order) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.merge(order);
            em.getTransaction().commit();
            return true;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Ошибка обновления заказа", e);
        } finally {
            em.close();
        }
    }

    @Override
    public boolean delete(Long id) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            Order order = em.find(Order.class, id);
            if (order != null) {
                em.remove(order);
                em.getTransaction().commit();
                return true;
            }
            em.getTransaction().rollback();
            return false;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Ошибка удаления заказа", e);
        } finally {
            em.close();
        }
    }

    @Override
    public List<Order> findByStatus(String status) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            TypedQuery<Order> query = em.createQuery(
                    "SELECT o FROM Order o WHERE o.status = :status ORDER BY o.orderDate DESC",
                    Order.class);
            query.setParameter("status", OrderStatus.valueOf(status));
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public List<Order> findByCustomer(String customerName) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            TypedQuery<Order> query = em.createQuery(
                    "SELECT o FROM Order o WHERE LOWER(o.customerName) LIKE LOWER(:name) " +
                            "ORDER BY o.orderDate DESC", Order.class);
            query.setParameter("name", "%" + customerName + "%");
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public List<Order> findCompletedBetween(LocalDateTime startDate, LocalDateTime endDate) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            TypedQuery<Order> query = em.createQuery(
                    "SELECT o FROM Order o WHERE o.status = :status " +
                            "AND o.completionDate BETWEEN :startDate AND :endDate " +
                            "ORDER BY o.completionDate DESC", Order.class);
            query.setParameter("status", OrderStatus.COMPLETED);
            query.setParameter("startDate", startDate);
            query.setParameter("endDate", endDate);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public double getTotalRevenue(LocalDateTime startDate, LocalDateTime endDate) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            TypedQuery<Double> query = em.createQuery(
                    "SELECT COALESCE(SUM(o.totalPrice), 0) FROM Order o " +
                            "WHERE o.status = :status AND o.completionDate BETWEEN :startDate AND :endDate",
                    Double.class);
            query.setParameter("status", OrderStatus.COMPLETED);
            query.setParameter("startDate", startDate);
            query.setParameter("endDate", endDate);
            return query.getSingleResult();
        } finally {
            em.close();
        }
    }

    @Override
    public boolean changeOrderStatus(Long orderId, OrderStatus newStatus) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            Order order = em.find(Order.class, orderId);
            if (order == null) {
                em.getTransaction().rollback();
                return false;
            }

            // Нельзя изменить статус выполненного или отмененного заказа
            if (order.getStatus() == OrderStatus.COMPLETED ||
                    order.getStatus() == OrderStatus.CANCELLED) {
                em.getTransaction().rollback();
                return false;
            }

            order.setStatus(newStatus);
            if (newStatus == OrderStatus.COMPLETED) {
                order.setCompletionDate(LocalDateTime.now());
            }
            em.merge(order);
            em.getTransaction().commit();
            return true;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Ошибка изменения статуса заказа", e);
        } finally {
            em.close();
        }
    }

    @Override
    public int getCountByStatus(String status) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            TypedQuery<Long> query = em.createQuery(
                    "SELECT COUNT(o) FROM Order o WHERE o.status = :status", Long.class);
            query.setParameter("status", OrderStatus.valueOf(status));
            return query.getSingleResult().intValue();
        } finally {
            em.close();
        }
    }

    @Override
    public List<Order> findByBookId(Long bookId) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            TypedQuery<Order> query = em.createQuery(
                    "SELECT o FROM Order o WHERE o.bookId = :bookId ORDER BY o.orderDate DESC",
                    Order.class);
            query.setParameter("bookId", bookId);
            return query.getResultList();
        } finally {
            em.close();
        }
    }
}