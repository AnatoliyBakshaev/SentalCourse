package com.library.library_management_system.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.*;

@Data
@NoArgsConstructor
public class RentalRequestDto {

    @NotNull(message = "ID книги обязателен")
    private Long bookId;

    @Size(max = 500, message = "Примечание не более 500 символов")
    private String notes;
}