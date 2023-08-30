package ru.practicum.ewm.event.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.StateEvent;
import ru.practicum.ewm.user.model.User;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface EventRepository extends JpaRepository<Event, Long>, QuerydslPredicateExecutor<Event> {
    Set<Event> findAllByIdIn(Set<Long> ids);

    Optional<Event> findByIdAndState(Long id, StateEvent state);

    List<Event> findAllByInitiator(User initiator, Pageable pageable);

    long countAllByCategory(Category category);

}
