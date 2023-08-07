package ru.practicum.ewm.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EndpointHitDto {
    private Long id;
    @NotBlank(message = "Поле app не может быть пустым")
    @Size(min = 1, max = 100, message = "app должно быть до 100 символов")
    private String app;
    @NotBlank(message = "Поле uri не может быть пустым")
    @Size(min = 1, max = 200, message = "uri должно быть до 200 символов")
    private String uri;
    @NotBlank(message = "Поле ip не может быть пустым")
    @Size(min = 7, max = 15, message = "ip должен быть от 7 до 15 символов")
    @Pattern(regexp = "^[0-9]+[.][0-9]+[.][0-9]+[.][0-9]+$", message = "Неверный формат ip")
    private String ip;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;
}
