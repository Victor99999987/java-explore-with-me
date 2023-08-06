package ru.practicum.ewm.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

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
    @Pattern(regexp = "^[0-9]+[.][0-9]+[.][0-9]+[.][0-9]$", message = "Неверный формат ip")
    private String ip;
    @NotBlank(message = "Поле timestamp не может быть пустым")
    private String timestamp;
}
