package ru.practicum.ewm.event.dto;

import lombok.*;
import ru.practicum.ewm.event.model.StateUpdateRequest;

import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EventRequestStatusUpdateRequest {
    @NotNull
    List<Long> requestIds;

    @NotNull
    StateUpdateRequest status;
}
