package ru.practicum.ewm.controller;

import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.EndpointHitDto;
import ru.practicum.ewm.dto.ViewStatsDto;
import ru.practicum.ewm.service.StatService;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@Validated
@RestController
@RequestMapping("/")
public class StatController {

    private final StatService statService;

    public StatController(StatService statService) {
        this.statService = statService;
    }

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public void addHit(@NotNull @Valid @RequestBody EndpointHitDto endpointHitDto) {
        statService.addHit(endpointHitDto);
    }

    @GetMapping("/stats")
    public List<ViewStatsDto> getStats(@NotNull(message = "не указан параметр start") @RequestParam String start,
                                       @NotNull(message = "не указан параметр end") @RequestParam String end,
                                       @RequestParam(defaultValue = "") String uris,
                                       @RequestParam(defaultValue = "false") boolean unique) {
        return statService.getStats(start, end, uris, unique);
    }

}
