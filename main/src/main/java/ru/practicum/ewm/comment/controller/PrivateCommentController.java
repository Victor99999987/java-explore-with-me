package ru.practicum.ewm.comment.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.comment.dto.CommentDto;
import ru.practicum.ewm.comment.dto.NewCommentDto;
import ru.practicum.ewm.comment.service.CommentService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Validated
@RestController
@Slf4j
@RequiredArgsConstructor
public class PrivateCommentController {
    private final CommentService commentService;

    @PostMapping("/users/{userId}/events/{eventId}/comments")
    @ResponseStatus(HttpStatus.CREATED)
    CommentDto addNewComment(@PathVariable Long userId,
                             @PathVariable Long eventId,
                             @Valid @RequestBody NewCommentDto newCommentDto) {
        log.info("Получен запрос на эндпоинт POST /users/{}/events/{}/comments", userId, eventId);
        return commentService.addNewComment(userId, eventId, newCommentDto);
    }

    @GetMapping("/users/{userId}/comments")
    List<CommentDto> getAllCommentByUser(@PathVariable Long userId,
                                         @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                         @RequestParam(defaultValue = "10") @Positive int size) {
        log.info("Получен запрос на эндпоинт GET /users/{}/comments", userId);
        return commentService.getAllCommentByUser(userId, from, size);
    }

    @PatchMapping("/users/{userId}/events/{eventId}/comments/{id}")
    CommentDto patchCommentByUser(@PathVariable Long userId,
                                  @PathVariable Long eventId,
                                  @PathVariable Long id,
                                  @Valid @RequestBody NewCommentDto newCommentDto) {
        log.info("Получен запрос на эндпоинт PATCH /users/{}/events/{}/comments/{}", userId, eventId, id);
        return commentService.patchCommentByUser(userId, eventId, id, newCommentDto);
    }

    @PatchMapping("/users/{userId}/events/{eventId}/comments/{id}/answer")
    CommentDto patchAnswerByUser(@PathVariable Long userId,
                                 @PathVariable Long eventId,
                                 @PathVariable Long id,
                                 @Valid @RequestBody NewCommentDto newCommentDto) {
        log.info("Получен запрос на эндпоинт PATCH /users/{}/events/{}/comments/{}/answer", userId, eventId, id);
        return commentService.patchAnswerByUser(userId, eventId, id, newCommentDto);
    }

}

