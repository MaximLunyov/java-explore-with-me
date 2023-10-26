package ru.practicum.ewm.event.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.category.Category;
import ru.practicum.ewm.category.CategoryRepository;
import ru.practicum.ewm.event.SearchEventParams;
import ru.practicum.ewm.event.dto.*;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.EventSort;
import ru.practicum.ewm.event.model.EventState;
import ru.practicum.ewm.event.model.EventStateAction;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.exceptions.CantDoException;
import ru.practicum.ewm.exceptions.NotFoundException;
import ru.practicum.ewm.location.Location;
import ru.practicum.ewm.location.LocationMapper;
import ru.practicum.ewm.location.LocationRepository;
import ru.practicum.ewm.request.RequestRepository;
import ru.practicum.ewm.user.User;
import ru.practicum.ewm.user.UserRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final CategoryRepository categoryRepository;
    private final LocationRepository locationRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final RequestRepository requestRepository;
    private final EventMapper eventMapper;
    private final StatService statService;

    @Override
    @Transactional
    public EventFullDto create(NewEventDto newEventDto, Long userId) {
        if (newEventDto.getRequestModeration() == null) {
            newEventDto.setRequestModeration(true);
        }

        if (newEventDto.getParticipantLimit() == null) {
            newEventDto.setParticipantLimit(0);
        }

        if (newEventDto.getPaid() == null) {
            newEventDto.setPaid(false);
        }
        User initiator = userExistsAndGet(userId);
        Category category = categoryCheckAndGet(newEventDto.getCategory());
        Location location = checkFindLocation(LocationMapper.toLocation(newEventDto.getLocation()));

        Event event = eventMapper.toEvent(newEventDto, initiator, category, location, EventState.PENDING);

        EventFullDto eventFullDto = mapToFullDtoWithViewsAndRequests(eventRepository.save(event));
        log.info("Создан Event c ID {}.", eventFullDto.getId());

        return eventFullDto;
    }

    private User userExistsAndGet(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(User.class, userId));
    }

    private Location checkFindLocation(Location location) {
        return locationRepository.findByLatAndLon(location.getLat(), location.getLon())
                .orElseGet(() -> locationRepository.save(location));
    }

    private Category categoryCheckAndGet(Long catId) {
        return categoryRepository.findById(catId)
                .orElseThrow(() -> new NotFoundException(Category.class, catId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventShortDto> getAllByInitiatorId(Long userId, Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from / size, size);
        List<EventShortDto> events = mapToShortDtoWithViewsAndRequests(
                eventRepository.findEventsByInitiatorId(userId, pageable));
        log.info("Получен список Events from={} size={} от инициатора User с id {}", from, size, userId);
        return events;
    }

    @Override
    @Transactional
    public EventFullDto updateByInitiator(UpdateEventDto updatedEvent, Long eventId, Long userId) {
        userExists(userId);
        Event event = checkIfOwnEventExistsAndGet(eventId, userId);
        userCanUpdateChecker(event);
        updateNotNullFields(updatedEvent, event);

        if (updatedEvent.getParticipantLimit() != null) {
            event.setParticipantLimit(updatedEvent.getParticipantLimit());
        }

        if (updatedEvent.getStateAction() != null) {
            EventStateAction stateAction = updatedEvent.getStateAction();
            if (stateAction == EventStateAction.CANCEL_REVIEW) {
                event.setState(EventState.CANCELED);
            } else if (stateAction == EventStateAction.SEND_TO_REVIEW) {
                event.setState(EventState.PENDING);
            }
        }
        EventFullDto eventFullDto = mapToFullDtoWithViewsAndRequests(eventRepository.save(event));
        log.info("Обновлен Event с id {} от User c id {}", eventId, userId);
        return eventFullDto;
    }

    private Event checkIfOwnEventExistsAndGet(Long eventId, Long userId) {
        return eventRepository.findEventByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new NotFoundException(Event.class, eventId));
    }

    private void userCanUpdateChecker(Event event) {
        if (event.getState().equals(EventState.PUBLISHED)) {
            throw new CantDoException("Можно изменить только отложенные или отмененные события");
        }
    }

    @Override
    @Transactional
    public EventFullDto updateByAdmin(UpdateEventDto updatedEvent, Long eventId) {
        Event event = eventCheckAndGet(eventId);
        adminCanUpdateChecker(event);
        updateNotNullFields(updatedEvent, event);

        if (updatedEvent.getParticipantLimit() != null) {
            updateLimitExist(updatedEvent.getParticipantLimit(), requestRepository.getConfirmedRequests(eventId));
            event.setParticipantLimit(updatedEvent.getParticipantLimit());
        }

        if (updatedEvent.getStateAction() != null) {

            EventStateAction stateAction = updatedEvent.getStateAction();
            if (stateAction == EventStateAction.PUBLISH_EVENT) {
                event.setState(EventState.PUBLISHED);
                event.setPublishedOn(LocalDateTime.now());
            } else if (stateAction == EventStateAction.REJECT_EVENT) {
                event.setState(EventState.CANCELED);
            }
        }
        EventFullDto eventFullDto = mapToFullDtoWithViewsAndRequests(eventRepository.save(event));
        log.info("Обновлен Event с id {} от admin", eventId);
        return eventFullDto;
    }

    private Event eventCheckAndGet(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(Event.class, eventId));
    }

    private void adminCanUpdateChecker(Event event) {
        if (!event.getState().equals(EventState.PENDING)) {
            throw new CantDoException(
                    "Не удается опубликовать событие, потому что оно находится в неправильном состоянии: PUBLISHED");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventFullDto> getAllEventsByAdmin(List<Long> users, List<EventState> states, List<Long> categories,
                                                  LocalDateTime rangeStart, LocalDateTime rangeEnd, Integer from, Integer size) {
        List<EventFullDto> elements = mapToFullDtoWithViewsAndRequests(eventRepository.findEventsByAdmin(users, states,
                categories, rangeStart, rangeEnd, from, size));
        log.info(
                "Получен список events от admin с параметрами: users={}, states={}, categories={}, start={}, end={}, from={}, size={}",
                users, states, categories, rangeStart, rangeEnd, from, size);
        return elements;
    }

    @Override
    @Transactional(readOnly = true)
    public EventFullDto getEventByPublic(Long eventId, String uri, String ip) {
        Event event = publishedEventCheckAndGet(eventId);
        log.info("Получен event с id {} от User public", eventId);
        statService.hit(uri, ip);
        return mapToFullDtoWithViewsAndRequests(event);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventShortDto> getAllEventsByPublic(SearchEventParams searchEventParams, String uri, String ip) {
        String text = searchEventParams.getText();
        List<Long> categories = searchEventParams.getCategories();
        Boolean paid = searchEventParams.getPaid();
        LocalDateTime rangeStart = searchEventParams.getRangeStart();
        LocalDateTime rangeEnd = searchEventParams.getRangeEnd();
        Boolean onlyAvailable = searchEventParams.getOnlyAvailable();
        EventSort sort = searchEventParams.getSort();
        Integer from = searchEventParams.getFrom();
        Integer size = searchEventParams.getSize();
        startTimeExist(rangeStart, rangeEnd);

        List<Event> events = eventRepository.findEventsByPublic(text, categories, paid, rangeStart, rangeEnd, from,
                size, sort);

        Map<Long, Integer> eventLimits = new HashMap<>();
        events.forEach(e -> eventLimits.put(e.getId(), e.getParticipantLimit()));

        List<EventShortDto> eventsWithViewsAndRequests = mapToShortDtoWithViewsAndRequests(events);

        if (onlyAvailable) {
            eventsWithViewsAndRequests = eventsWithViewsAndRequests.stream()
                    .filter(e -> eventLimits.get(e.getId()) == 0
                            || eventLimits.get(e.getId()) > e.getConfirmedRequests())
                    .collect(Collectors.toList());
        }

        if (sort != null && sort.equals(EventSort.VIEWS)) {
            eventsWithViewsAndRequests.sort(Comparator.comparing(EventShortDto::getViews));
        }

        log.info(
                "Получен список events от user public с параметрами: text={}, categories={}, paid={}, start={}, end={}, "
                        + "onlyAvailable={},sort={}, from={}, size={}",
                text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size);
        statService.hit(uri, ip);

        return eventsWithViewsAndRequests;
    }

    private void startTimeExist(LocalDateTime start, LocalDateTime end) {
        if (start != null && end != null && end.isBefore(start)) {
            throw new IllegalStateException(
                    "Неправильный временной интервал. Начальный параметр должен быть раньше конечного параметра");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public EventFullDto getEventByIdAndInitiatorId(Long eventId, Long userId) {
        EventFullDto eventFullDto = mapToFullDtoWithViewsAndRequests(checkIfOwnEventExistsAndGet(eventId, userId));
        log.info("Получен Event c ID {} от Initiator c ID {}.", eventId, userId);
        return eventFullDto;
    }

    private List<EventShortDto> mapToShortDtoWithViewsAndRequests(List<Event> events) {
        Map<Long, Long> views = statService.getViews(events);
        Map<Long, Long> confirmedRequests = statService.getConfirmedRequests(events);

        return events.stream()
                .map(e -> eventMapper.toEventShortDto(e, confirmedRequests.getOrDefault(e.getId(), 0L),
                        views.getOrDefault(e.getId(), 0L)))
                .collect(Collectors.toList());
    }

    private List<EventFullDto> mapToFullDtoWithViewsAndRequests(List<Event> events) {
        Map<Long, Long> views = statService.getViews(events);
        Map<Long, Long> confirmedRequests = statService.getConfirmedRequests(events);

        return events.stream()
                .map(e -> eventMapper.toEventFullDto(e, confirmedRequests.getOrDefault(e.getId(), 0L),
                        views.getOrDefault(e.getId(), 0L)))
                .collect(Collectors.toList());
    }

    private void updateNotNullFields(UpdateEventDto updatedEvent, Event event) {
        if (updatedEvent.getAnnotation() != null) {
            event.setAnnotation(updatedEvent.getAnnotation());
        }
        if (updatedEvent.getCategory() != null) {
            event.setCategory(categoryCheckAndGet(updatedEvent.getCategory()));
        }
        if (updatedEvent.getEventDate() != null) {
            event.setEventDate(updatedEvent.getEventDate());
        }
        if (updatedEvent.getRequestModeration() != null) {
            event.setRequestModeration(updatedEvent.getRequestModeration());
        }
        if (updatedEvent.getPaid() != null) {
            event.setPaid(updatedEvent.getPaid());
        }
        if (updatedEvent.getTitle() != null) {
            event.setTitle(updatedEvent.getTitle());
        }
        if (updatedEvent.getDescription() != null) {
            event.setDescription(updatedEvent.getDescription());
        }
        if (updatedEvent.getLocation() != null) {
            event.setLocation(checkFindLocation(LocationMapper.toLocation(updatedEvent.getLocation())));
        }
        log.info("Event c ID {} обновлен на параметры {}.", event.getId(), updatedEvent);
    }

    private void updateLimitExist(Integer newLimit, Long confirmedReq) {
        if (newLimit != 0 && newLimit < confirmedReq) {
            throw new CantDoException("Новый лимит участников не может быть меньше количества подтвержденных запросов");
        }
    }

    private EventFullDto mapToFullDtoWithViewsAndRequests(Event event) {
        return mapToFullDtoWithViewsAndRequests(Collections.singletonList(event)).get(0);
    }

    private void userExists(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException(User.class, userId);
        }
    }

    private Event publishedEventCheckAndGet(Long eventId) {
        return eventRepository.getEventIfPublished(eventId)
                .orElseThrow(() -> new NotFoundException(Event.class, eventId));
    }

}