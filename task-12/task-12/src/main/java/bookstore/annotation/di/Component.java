package bookstore.annotation.di;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Аннотация для пометки класса как компонента,
 * который будет управляться DI контейнером.
 *
 * Пример использования:
 * @Component
 * public class BookStoreService { ... }
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Component {

    /**
     * Опциональное имя компонента.
     * Используется для идентификации, если есть несколько реализаций одного интерфейса.
     */
    String value() default "";

    /**
     * Определяет, является ли компонент синглтоном.
     * true - один экземпляр на весь контейнер
     * false - каждый раз создается новый экземпляр
     */
    boolean singleton() default true;
}
