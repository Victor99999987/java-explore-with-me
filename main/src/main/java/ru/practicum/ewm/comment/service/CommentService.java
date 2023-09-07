package ru.practicum.ewm.comment.service;

import ru.practicum.ewm.comment.dto.CommentDto;
import ru.practicum.ewm.comment.dto.NewCommentDto;
import ru.practicum.ewm.comment.dto.UpdateCommentAdminDto;

import java.time.LocalDateTime;
import java.util.List;

public interface CommentService {

    List<CommentDto> getAllCommentsByEvent(Long eventId, int from, int size);

    CommentDto getUserCommentById(Long eventId, Long id);

    CommentDto addNewComment(Long userId, Long eventId, NewCommentDto newCommentDto);

    List<CommentDto> getAllCommentByUser(Long userId, int from, int size);

    CommentDto patchCommentByUser(Long userId, Long eventId, Long id, NewCommentDto newCommentDto);

    void deleteCommentById(Long id);

    CommentDto patchAnswerByUser(Long userId, Long eventId, Long id, NewCommentDto newCommentDto);

    CommentDto patchCommentByAdmin(Long id, UpdateCommentAdminDto updateCommentAdminDto);

    List<CommentDto> getAllCommentsByFilter(List<Long> events, LocalDateTime rangeStart, LocalDateTime rangeEnd, int from, int size);
}
