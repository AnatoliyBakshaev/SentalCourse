package com.library.library_management_system.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.*;

@Data
@NoArgsConstructor
public class UserProfileUpdateDto {

    @NotBlank(message = "Имя обязательно")
    @Size(min = 2, max = 100, message = "Имя должно быть от 2 до 100 символов")
    private String fullName;

    @NotBlank(message = "Email обязателен")
    @Email(message = "Введите корректный email")
    private String email;

    private String phone;
    private String address;
}