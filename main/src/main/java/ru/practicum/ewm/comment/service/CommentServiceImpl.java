package ru.practicum.ewm.comment.service;

import com.querydsl.core.types.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.comment.dto.CommentDto;
import ru.practicum.ewm.comment.dto.NewCommentDto;
import ru.practicum.ewm.comment.dto.UpdateCommentAdminDto;
import ru.practicum.ewm.comment.mapper.CommentMapper;
import ru.practicum.ewm.comment.model.Comment;
import ru.practicum.ewm.comment.model.QComment;
import ru.practicum.ewm.comment.repository.CommentRepository;
import ru.practicum.ewm.common.ConflictException;
import ru.practicum.ewm.common.NotFoundException;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.StateEvent;
import ru.practicum.ewm.event.service.EventService;
import ru.practicum.ewm.user.model.User;
import ru.practicum.ewm.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private static final Sort SORT_BY_ID = Sort.by(Sort.Direction.ASC, "id");
    private final CommentRepository commentRepository;
    private final EventService eventService;
    private final UserService userService;

    @Transactional
    @Override
    public List<CommentDto> getAllCommentsByEvent(Long eventId, int from, int size) {
        Event event = eventService.getEventById(eventId);

        if (event.getState() != StateEvent.PUBLISHED) {
            throw new ConflictException(String.format("Событие с id %d недоступно для просмотра", eventId));
        }

        Pageable pageable = PageRequest.of(from > 0 ? from / size : 0, size, SORT_BY_ID);

        List<Comment> comments = commentRepository.findAllByEvent(event, pageable);

        return comments.stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public CommentDto getUserCommentById(Long eventId, Long id) {
        Event event = eventService.getEventById(eventId);

        if (event.getState() != StateEvent.PUBLISHED) {
            throw new ConflictException(String.format("Событие с id %d недоступно для просмотра", eventId));
        }

        Comment comment = getCommentById(id);

        if (!Objects.equals(comment.getEvent().getId(), event.getId())) {
            throw new ConflictException(String.format("Комментарий с id %d не принадлежит событию с id %d", id, eventId));
        }

        return CommentMapper.toCommentDto(comment);
    }

    @Transactional
    @Override
    public CommentDto addNewComment(Long userId, Long eventId, NewCommentDto newCommentDto) {
        User user = userService.getUserById(userId);

        Event event = eventService.getEventById(eventId);

        if (event.getState() != StateEvent.PUBLISHED) {
            throw new ConflictException(String.format("Событие с id %d недоступно для комментирования", eventId));
        }

        Comment comment = Comment.builder()
                .text(newCommentDto.getText())
                .created(LocalDateTime.now())
                .event(event)
                .author(user)
                .build();

        comment = commentRepository.save(comment);
        return CommentMapper.toCommentDto(comment);
    }

    @Transactional
    @Override
    public List<CommentDto> getAllCommentByUser(Long userId, int from, int size) {
        User user = userService.getUserById(userId);

        Pageable pageable = PageRequest.of(from > 0 ? from / size : 0, size, SORT_BY_ID);

        List<Comment> comments = commentRepository.findAllByAuthor(user, pageable);

        return comments.stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public CommentDto patchCommentByUser(Long userId, Long eventId, Long id, NewCommentDto newCommentDto) {
        User user = userService.getUserById(userId);

        Event event = eventService.getEventById(eventId);

        if (event.getState() != StateEvent.PUBLISHED) {
            throw new ConflictException(String.format("Событие с id %d недоступно для комментирования", eventId));
        }

        Comment comment = getCommentById(id);

        if (!Objects.equals(comment.getEvent().getId(), event.getId())) {
            throw new ConflictException(String.format("Комментарий с id %d не принадлежит событию с id %d", id, eventId));
        }

        if (comment.getCreated().plusHours(1).isBefore(LocalDateTime.now())) {
            throw new ConflictException("Время для редактирования ограничено 1 часом. Редактирование невозможно");
        }

        comment.setText(newCommentDto.getText());

        comment = commentRepository.save(comment);

        return CommentMapper.toCommentDto(comment);
    }

    @Transactional
    @Override
    public void deleteCommentById(Long id) {
        if (!commentRepository.existsById(id)) {
            throw new NotFoundException(String.format("Комментарий с id %d не найден", id));
        }
        commentRepository.deleteById(id);
    }

    @Transactional
    @Override
    public CommentDto patchAnswerByUser(Long userId, Long eventId, Long id, NewCommentDto newCommentDto) {
        User user = userService.getUserById(userId);

        Event event = eventService.getEventById(eventId);

        if (!Objects.equals(event.getInitiator().getId(), user.getId())) {
            throw new ConflictException("Отвечать на комментарии может только инициатор события");
        }

        if (event.getState() != StateEvent.PUBLISHED) {
            throw new ConflictException(String.format("Событие с id %d недоступно для комментирования", eventId));
        }

        Comment comment = getCommentById(id);

        if (!Objects.equals(comment.getEvent().getId(), event.getId())) {
            throw new ConflictException(String.format("Комментарий с id %d не принадлежит событию с id %d", id, eventId));
        }

        comment.setAnswer(newCommentDto.getText());
        comment = commentRepository.save(comment);

        return CommentMapper.toCommentDto(comment);
    }

    @Transactional
    @Override
    public CommentDto patchCommentByAdmin(Long id, UpdateCommentAdminDto updateCommentAdminDto) {
        Comment comment = getCommentById(id);

        if (updateCommentAdminDto.getText() != null) {
            if (updateCommentAdminDto.getText().isBlank()) {
                throw new IllegalArgumentException("Текст комментария не может состоять из пробелов");
            }
            comment.setText(updateCommentAdminDto.getText());
        }

        if (updateCommentAdminDto.getAnswer() != null) {
            if (updateCommentAdminDto.getAnswer().isBlank()) {
                throw new IllegalArgumentException("Текст ответа не может состоять из пробелов");
            }
            comment.setAnswer(updateCommentAdminDto.getAnswer());
        }

        comment = commentRepository.save(comment);

        return CommentMapper.toCommentDto(comment);
    }

    @Override
    public List<CommentDto> getAllCommentsByFilter(List<Long> events, LocalDateTime rangeStart, LocalDateTime rangeEnd, int from, int size) {
        if (rangeStart != null && rangeEnd != null) {
            if (rangeStart.isAfter(rangeEnd)) {
                throw new IllegalArgumentException("Дата окончания периода раньше даты начала.");
            }
        }

        QComment qComment = QComment.comment;
        Predicate predicate = qComment.id.isNotNull();
        if (events != null && events.size() > 0) {
            predicate = qComment.event.id.in(events).and(predicate);
        }
        if (rangeStart != null) {
            predicate = qComment.created.after(rangeStart);
        }
        if (rangeEnd != null) {
            predicate = qComment.created.before(rangeEnd);
        }
        Pageable pageable = PageRequest.of(from > 0 ? from / size : 0, size, SORT_BY_ID);

        List<Comment> comments = commentRepository.findAll(predicate, pageable).getContent();

        return comments.stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList());
    }

    public Comment getCommentById(Long id) {
        return commentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Комментарий с id %d не найден", id)));
    }

}
