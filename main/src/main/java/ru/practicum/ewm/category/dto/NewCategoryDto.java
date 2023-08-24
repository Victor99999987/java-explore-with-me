package ru.practicum.ewm.category.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NewCategoryDto {
    @NotBlank(message = "название должно быть указано и не должно состоять из пробелов")
    @Size(min = 1, max = 50, message = "название должно быть от 1 до 50 символов")
    private String name;
}
