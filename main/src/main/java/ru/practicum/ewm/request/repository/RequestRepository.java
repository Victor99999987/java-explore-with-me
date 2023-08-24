package ru.practicum.ewm.request.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.request.model.CountRequest;
import ru.practicum.ewm.request.model.Request;
import ru.practicum.ewm.request.model.StateRequest;
import ru.practicum.ewm.user.model.User;

import java.util.List;
import java.util.Optional;

public interface RequestRepository extends JpaRepository<Request, Long> {

    List<Request> findAllByRequester(User user, Sort sort);

    boolean existsByRequesterAndEvent(User user, Event event);

    Request findByRequesterAndEvent(User user, Event event);

    int countAllByEvent(Event event);

    Optional<Request> findByIdAndRequester(Long requestId, User requester);

    Long countAllByEventAndStatus(Event event, StateRequest status);

    List<Request> findAllByRequesterAndEvent(User requester, Event event, Sort sort);

    List<Request> findAllByEventAndIdIn(Event event, List<Long> requestIds);


    List<Request> findAllByEvent(Event event, Sort sortById);

    List<Request> findAllByEventInAndStatus(List<Event> events, StateRequest confirmed);

    @Query("select new ru.practicum.ewm.request.model.CountRequest(r.event.id, count(r.id)) " +
            "from Request as r " +
            "where r.status = 'CONFIRMED' " +
            "and event in :events " +
            "group by r.event.id")
    List<CountRequest> findAllCountsByConfirmedEventIn(List<Event> events);
}
