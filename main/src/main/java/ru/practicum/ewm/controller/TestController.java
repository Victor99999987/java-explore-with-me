package ru.practicum.ewm.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewm.client.StatClient;
import ru.practicum.ewm.dto.EndpointHitDto;
import ru.practicum.ewm.mapper.DateMapper;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@Validated
@RestController
@RequestMapping("/")
public class TestController {
    private final StatClient statClient;

    public TestController(StatClient statClient) {
        this.statClient = statClient;
    }

    @PostMapping("/test1")
    public ResponseEntity<Object> postEndpointForTest1(HttpServletRequest request) {
        EndpointHitDto endpointHitDto = EndpointHitDto.builder()
                .app("ewm-main-service")
                .uri(request.getRequestURI())
                .ip(request.getRemoteAddr())
                .timestamp(DateMapper.toStr(LocalDateTime.now()))
                .build();
        return statClient.sendHit(endpointHitDto);
    }

    @PostMapping("/test2")
    public ResponseEntity<Object> postEndpointForTest2(HttpServletRequest request) {
        EndpointHitDto endpointHitDto = EndpointHitDto.builder()
                .app("ewm-main-service")
                .uri(request.getRequestURI())
                .ip(request.getRemoteAddr())
                .timestamp(DateMapper.toStr(LocalDateTime.now()))
                .build();
        return statClient.sendHit(endpointHitDto);
    }

}
