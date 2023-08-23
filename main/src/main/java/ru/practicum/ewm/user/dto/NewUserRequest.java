package ru.practicum.ewm.user.dto;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NewUserRequest {
    @NotNull(message = "email должен быть указан")
    @Email(message = "неверный формат email")
    @Size(min = 6, max=254, message = "имя должно быть от 6 до 254 символов")
    private String email;
    @NotNull(message = "имя должно быть указано")
    @NotBlank(message = "имя не должно состоять из пробелов")
    @Size(min = 2, max=250, message = "имя должно быть от 2 до 250 символов")
    private String name;
}
