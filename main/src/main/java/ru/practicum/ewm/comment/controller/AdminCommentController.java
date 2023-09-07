package ru.practicum.ewm.comment.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.comment.dto.CommentDto;
import ru.practicum.ewm.comment.dto.UpdateCommentAdminDto;
import ru.practicum.ewm.comment.service.CommentService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;

@Validated
@RestController
@Slf4j
@RequiredArgsConstructor
public class AdminCommentController {
    private final CommentService commentService;

    @GetMapping("/admin/comments")
    List<CommentDto> getAllCommentsByFilter(@RequestParam(required = false) List<Long> events,
                                            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
                                            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
                                            @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                            @RequestParam(defaultValue = "10") @Positive int size) {
        log.info("Получен запрос на эндпоинт GET /events/{eventId}/comments");
        return commentService.getAllCommentsByFilter(events, rangeStart, rangeEnd, from, size);
    }

    @PatchMapping("/admin/comments/{id}")
    CommentDto patchCommentByAdmin(@PathVariable Long id,
                                   @Valid @RequestBody UpdateCommentAdminDto updateCommentAdminDto) {
        log.info("Получен запрос на эндпоинт PATCH admin/comments/{}", id);
        return commentService.patchCommentByAdmin(id, updateCommentAdminDto);
    }

    @DeleteMapping("/admin/comments/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteCommentById(@PathVariable Long id) {
        log.info("Получен запрос на эндпоинт DELETE admin/comments/{}", id);
        commentService.deleteCommentById(id);
    }

}
