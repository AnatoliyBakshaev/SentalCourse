package bookstore.annotation.di;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Альтернативная аннотация для внедрения зависимостей.
 * Может использоваться на полях, конструкторах и методах.
 *
 * Пример использования:
 * @Component
 * public class BookStoreController {
 *     @Autowired
 *     private IBookStoreService service;
 * }
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.CONSTRUCTOR, ElementType.METHOD})
public @interface Autowired {

    boolean required() default true;

    String value() default "";
}