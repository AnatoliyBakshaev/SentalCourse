package bookstore.annotation.di;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Аннотация для указания имени компонента или зависимости.
 * Используется для разрешения конфликтов, когда есть несколько реализаций.
 *
 * Пример использования:
 * @Component
 * @Named("csvService")
 * public class CsvImportExportService { ... }
 *
 * // Внедрение по имени
 * @Inject
 * @Named("csvService")
 * private CsvImportExportService csvService;
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.FIELD, ElementType.PARAMETER})
public @interface Named {
    String value();
}
