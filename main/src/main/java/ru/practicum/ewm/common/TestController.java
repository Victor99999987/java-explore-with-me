package ru.practicum.ewm.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.client.StatClient;
import ru.practicum.ewm.dto.EndpointHitDto;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

@Validated
@RestController
@Slf4j
@RequestMapping("/")
public class TestController {
    private final StatClient statClient;

    public TestController(StatClient statClient) {
        this.statClient = statClient;
    }

    @PostMapping("/test-hit")
    public ResponseEntity<Object> postEndpointForTest(HttpServletRequest request) {
        EndpointHitDto endpointHitDto = EndpointHitDto.builder()
                .app("ewm-main-service")
                .uri(request.getRequestURI())
                .ip(request.getRemoteAddr())
                .timestamp(LocalDateTime.now())
                .build();
        log.info("Получен запрос на тестовый эндпоинт {}", request.getRequestURI());
        return statClient.sendHit(endpointHitDto);
    }

    @GetMapping("/test-stats")
    public ResponseEntity<Object> getEndpointForTest(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime start,
                                                     @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime end,
                                                     @RequestParam(required = false) List<String> uris,
                                                     @RequestParam(defaultValue = "false") boolean unique,
                                                     HttpServletRequest request) {
        log.info("Получен запрос на тестовый эндпоинт {}", request.getRequestURI());
        return statClient.getStats(start, end, uris, unique);
    }

}
