package ru.practicum.ewm.comment.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NewCommentDto {
    @NotBlank(message = "поле text должно быть указано")
    @Size(min = 3, max = 2000, message = "поле text должно быть от 3 до 2000 символов")
    private String text;
}
