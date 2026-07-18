package bookstore.config;

import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class DatabaseConfig {

    @Value("${db.url}")
    private String dbUrl;

    @Value("${db.username}")
    private String dbUsername;

    @Value("${db.password}")
    private String dbPassword;

    public void runMigrations() {
        try {
            Flyway flyway = Flyway.configure()
                    .dataSource(dbUrl, dbUsername, dbPassword)
                    .locations("classpath:db/migration")
                    .baselineOnMigrate(true)
                    .load();

            flyway.migrate();
            System.out.println("Flyway миграции выполнены успешно");
        } catch (Exception e) {
            System.err.println("Ошибка выполнения Flyway миграций: " + e.getMessage());
            e.printStackTrace();
        }
    }
}