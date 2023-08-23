package ru.practicum.ewm.event.dto;

import lombok.*;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LocationDto {
    @NotNull(message = "поле lat должно быть указано")
    private double lat;

    @NotNull(message = "поле lon должно быть указано")
    private double lon;
}
