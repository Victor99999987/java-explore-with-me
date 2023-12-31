package ru.practicum.ewm.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NewEventDto {
    @NotBlank(message = "поле annotation должно быть указано и не состоять из пробелов")
    @Size(min = 20, max = 2000, message = "поле annotation должно быть от 20 до 2000 символов")
    private String annotation;

    @NotNull(message = "поле category должно быть указано")
    private Long category;

    @NotBlank(message = "поле description должно быть указано")
    @Size(min = 20, max = 7000, message = "поле description должно быть от 20 до 7000 символов")
    private String description;

    @NotNull(message = "поле eventDate должно быть указано")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;

    @NotNull(message = "поле location должно быть указано")
    @Valid
    private LocationDto location;

    private Boolean paid;

    @PositiveOrZero
    private Integer participantLimit;

    private Boolean requestModeration;

    @NotBlank(message = "поле title должно быть указано")
    @Size(min = 3, max = 120, message = "поле title должно быть от 3 до 120 символов")
    private String title;
}
