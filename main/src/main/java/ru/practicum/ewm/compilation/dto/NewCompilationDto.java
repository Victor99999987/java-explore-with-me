package ru.practicum.ewm.compilation.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NewCompilationDto {
    private Set<Long> events = new HashSet<>();
    private boolean pinned = false;
    @NotNull(message = "поле title должно быть указано")
    @NotBlank(message = "поле title не должно состоять из пробелов")
    @Size(min = 1, max = 50, message = "поле title должно содержать от 1 до 50 символов")
    private String title;
}
