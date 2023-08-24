package ru.practicum.ewm.event.dto;

import lombok.*;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LocationDto {
    @Min(-90)
    @Max(90)
    private double lat;

    @Min(-180)
    @Max(180)
    private double lon;
}
