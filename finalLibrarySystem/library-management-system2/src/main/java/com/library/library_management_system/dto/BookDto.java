package com.library.library_management_system.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.*;
import java.util.Set;

@Data
@NoArgsConstructor
public class BookDto {

    private Long id;

    @NotBlank(message = "Название книги обязательно")
    @Size(min = 1, max = 200, message = "Название должно быть от 1 до 200 символов")
    private String title;

    @NotBlank(message = "Автор обязателен")
    @Size(min = 1, max = 100, message = "Автор должен быть от 1 до 100 символов")
    private String author;

    @Size(max = 20, message = "ISBN не более 20 символов")
    private String isbn;

    private String publisher;
    private Integer publicationYear;

    @Size(max = 2000, message = "Описание не более 2000 символов")
    private String description;

    @Min(value = 1, message = "Количество копий должно быть не менее 1")
    private Integer totalCopies = 1;

    private Integer availableCopies;

    private Set<Long> categoryIds;
}