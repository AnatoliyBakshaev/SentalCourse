package bookstore.annotation.di;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Аннотация для внедрения зависимости в поле.
 *
 * Пример использования:
 * @Component
 * public class BookStoreController {
 *     @Inject
 *     private IBookStoreService service;
 * }
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Inject {

    /**
     * Опциональное имя зависимости.
     * Используется, когда нужно внедрить конкретную реализацию.
     */
    String value() default "";

    /**
     * Определяет, обязательна ли зависимость.
     * true - если зависимость не найдена, будет ошибка
     * false - если зависимость не найдена, поле останется null
     */
    boolean required() default true;
}