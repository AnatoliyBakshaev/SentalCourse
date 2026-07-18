package bookstore.dao.impl;

import bookstore.dao.BookDAO;
import bookstore.model.Book;
import bookstore.model.enums.BookStatus;
import bookstore.utils.JpaUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class BookDAOJpaImpl implements BookDAO {

    @Override
    public Book getById(Long id) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            return em.find(Book.class, id);
        } finally {
            em.close();
        }
    }

    @Override
    public List<Book> getAll() {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            TypedQuery<Book> query = em.createQuery(
                    "SELECT b FROM Book b ORDER BY b.id", Book.class);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public List<Book> findAll() {
        return getAll();
    }

    @Override
    public boolean save(Book book) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(book);
            em.getTransaction().commit();
            return true;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Ошибка сохранения книги", e);
        } finally {
            em.close();
        }
    }

    @Override
    public boolean update(Book book) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.merge(book);
            em.getTransaction().commit();
            return true;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Ошибка обновления книги", e);
        } finally {
            em.close();
        }
    }

    @Override
    public boolean delete(Long id) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            Book book = em.find(Book.class, id);
            if (book != null) {
                em.remove(book);
                em.getTransaction().commit();
                return true;
            }
            em.getTransaction().rollback();
            return false;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Ошибка удаления книги", e);
        } finally {
            em.close();
        }
    }

    @Override
    public Book findByIsbn(String isbn) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            TypedQuery<Book> query = em.createQuery(
                    "SELECT b FROM Book b WHERE b.isbn = :isbn", Book.class);
            query.setParameter("isbn", isbn);
            List<Book> result = query.getResultList();
            return result.isEmpty() ? null : result.get(0);
        } finally {
            em.close();
        }
    }

    @Override
    public List<Book> findByStatus(String status) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            TypedQuery<Book> query = em.createQuery(
                    "SELECT b FROM Book b WHERE b.status = :status ORDER BY b.title", Book.class);
            query.setParameter("status", BookStatus.valueOf(status));
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public List<Book> findByAuthor(String author) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            TypedQuery<Book> query = em.createQuery(
                    "SELECT b FROM Book b WHERE LOWER(b.author) LIKE LOWER(:author) ORDER BY b.title",
                    Book.class);
            query.setParameter("author", "%" + author + "%");
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public List<Book> findOldBooks(int months) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            LocalDate thresholdDate = LocalDate.now().minus(months, ChronoUnit.MONTHS);

            TypedQuery<Book> query = em.createQuery(
                    "SELECT b FROM Book b WHERE b.status = :status AND b.receivedDate <= :thresholdDate " +
                            "ORDER BY b.receivedDate", Book.class);
            query.setParameter("status", BookStatus.IN_STOCK);
            query.setParameter("thresholdDate", thresholdDate);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public boolean updateQuantity(Long id, int quantity) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            Book book = em.find(Book.class, id);
            if (book != null) {
                book.setQuantity(quantity);
                if (quantity == 0) {
                    book.setStatus(BookStatus.OUT_OF_STOCK);
                } else if (quantity > 0 && book.getStatus() == BookStatus.OUT_OF_STOCK) {
                    book.setStatus(BookStatus.IN_STOCK);
                    book.setReceivedDate(LocalDate.now());
                }
                em.merge(book);
                em.getTransaction().commit();
                return true;
            }
            em.getTransaction().rollback();
            return false;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Ошибка обновления количества", e);
        } finally {
            em.close();
        }
    }

    @Override
    public List<Book> findByTitle(String title) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            TypedQuery<Book> query = em.createQuery(
                    "SELECT b FROM Book b WHERE LOWER(b.title) LIKE LOWER(:title) ORDER BY b.title",
                    Book.class);
            query.setParameter("title", "%" + title + "%");
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public List<Book> findByGenre(String genre) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            // Используем Criteria API для разнообразия
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Book> cq = cb.createQuery(Book.class);
            Root<Book> root = cq.from(Book.class);
            cq.select(root).where(cb.equal(root.get("genre"), genre));
            cq.orderBy(cb.asc(root.get("title")));

            return em.createQuery(cq).getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public int getCount() {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            TypedQuery<Long> query = em.createQuery(
                    "SELECT COUNT(b) FROM Book b", Long.class);
            return query.getSingleResult().intValue();
        } finally {
            em.close();
        }
    }
}