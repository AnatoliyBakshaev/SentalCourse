package bookstore.utils;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class JpaUtil {
    private static final Logger logger = LogManager.getLogger(JpaUtil.class);
    private static final EntityManagerFactory emf;

    static {
        try {
            emf = Persistence.createEntityManagerFactory("bookstorePU");
            logger.info("EntityManagerFactory успешно создан");
        } catch (Exception e) {
            logger.error("Ошибка создания EntityManagerFactory", e);
            throw new RuntimeException("Ошибка инициализации JPA", e);
        }
    }

    public static EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public static void close() {
        if (emf != null && emf.isOpen()) {
            emf.close();
            logger.info("EntityManagerFactory закрыт");
        }
    }
}