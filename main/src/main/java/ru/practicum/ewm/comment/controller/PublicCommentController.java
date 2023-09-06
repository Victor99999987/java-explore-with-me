package ru.practicum.ewm.comment.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewm.comment.dto.CommentDto;
import ru.practicum.ewm.comment.service.CommentService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Validated
@RestController
@Slf4j
@RequiredArgsConstructor
public class PublicCommentController {

    private final CommentService commentService;

    @GetMapping("/events/{eventId}/comments")
    List<CommentDto> getAllCommentsByEvent(@PathVariable Long eventId,
                                           @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                           @RequestParam(defaultValue = "10") @Positive int size) {
        log.info("Получен запрос на эндпоинт GET /events/{}/comments", eventId);
        return commentService.getAllCommentsByEvent(eventId, from, size);
    }

    @GetMapping("/events/{eventId}/comments/{id}")
    CommentDto getUserCommentById(@PathVariable Long eventId,
                                  @PathVariable Long id) {
        log.info("Получен запрос на эндпоинт GET /events/{}/comments/{}", eventId, id);
        return commentService.getUserCommentById(eventId, id);
    }
}
