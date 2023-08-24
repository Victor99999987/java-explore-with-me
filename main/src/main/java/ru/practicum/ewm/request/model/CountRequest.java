package ru.practicum.ewm.request.model;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CountRequest {
    private long eventId;
    private long count;
}
