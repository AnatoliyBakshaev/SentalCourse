package bookstore.dao.impl;

import bookstore.dao.RequestDAO;
import bookstore.model.Request;
import bookstore.utils.JpaUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import java.util.List;

public class RequestDAOJpaImpl implements RequestDAO {

    @Override
    public Request getById(Long id) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            return em.find(Request.class, id);
        } finally {
            em.close();
        }
    }

    @Override
    public List<Request> getAll() {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            TypedQuery<Request> query = em.createQuery(
                    "SELECT r FROM Request r ORDER BY r.id", Request.class);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public List<Request> findAll() {
        return getAll();
    }

    @Override
    public boolean save(Request request) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(request);
            em.getTransaction().commit();
            return true;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Ошибка сохранения запроса", e);
        } finally {
            em.close();
        }
    }

    @Override
    public boolean update(Request request) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.merge(request);
            em.getTransaction().commit();
            return true;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Ошибка обновления запроса", e);
        } finally {
            em.close();
        }
    }

    @Override
    public boolean delete(Long id) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            Request request = em.find(Request.class, id);
            if (request != null) {
                em.remove(request);
                em.getTransaction().commit();
                return true;
            }
            em.getTransaction().rollback();
            return false;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Ошибка удаления запроса", e);
        } finally {
            em.close();
        }
    }

    @Override
    public List<Request> findByBookId(Long bookId) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            TypedQuery<Request> query = em.createQuery(
                    "SELECT r FROM Request r WHERE r.bookId = :bookId ORDER BY r.requestDate DESC",
                    Request.class);
            query.setParameter("bookId", bookId);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public List<Request> findByCustomer(String customerName) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            TypedQuery<Request> query = em.createQuery(
                    "SELECT r FROM Request r WHERE LOWER(r.customerName) LIKE LOWER(:name) " +
                            "ORDER BY r.requestDate DESC", Request.class);
            query.setParameter("name", "%" + customerName + "%");
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public List<Request> findUnfulfilled() {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            TypedQuery<Request> query = em.createQuery(
                    "SELECT r FROM Request r WHERE r.fulfilled = false ORDER BY r.requestDate",
                    Request.class);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public List<Request> findFulfilled() {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            TypedQuery<Request> query = em.createQuery(
                    "SELECT r FROM Request r WHERE r.fulfilled = true ORDER BY r.requestDate DESC",
                    Request.class);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public boolean markFulfilled(Long requestId) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            Request request = em.find(Request.class, requestId);
            if (request != null) {
                request.setFulfilled(true);
                em.merge(request);
                em.getTransaction().commit();
                return true;
            }
            em.getTransaction().rollback();
            return false;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Ошибка отметки запроса как выполненного", e);
        } finally {
            em.close();
        }
    }

    @Override
    public int getCountByBookId(Long bookId) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            TypedQuery<Long> query = em.createQuery(
                    "SELECT COUNT(r) FROM Request r WHERE r.bookId = :bookId", Long.class);
            query.setParameter("bookId", bookId);
            return query.getSingleResult().intValue();
        } finally {
            em.close();
        }
    }

    @Override
    public int getUnfulfilledCount() {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            TypedQuery<Long> query = em.createQuery(
                    "SELECT COUNT(r) FROM Request r WHERE r.fulfilled = false", Long.class);
            return query.getSingleResult().intValue();
        } finally {
            em.close();
        }
    }
}