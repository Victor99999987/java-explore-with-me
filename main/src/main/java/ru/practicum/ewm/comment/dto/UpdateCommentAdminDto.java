package ru.practicum.ewm.comment.dto;

import lombok.*;

import javax.validation.constraints.Size;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateCommentAdminDto {
    @Size(min = 3, max = 2000, message = "поле text должно быть от 3 до 2000 символов")
    private String text;
    @Size(min = 3, max = 2000, message = "поле answer должно быть от 3 до 2000 символов")
    private String answer;
}
