package ru.practicum.ewm.event.service;


import ru.practicum.ewm.event.model.Event;

import java.util.List;
import java.util.Map;

public interface StatService {

    void hit(String uri, String ip);

    Map<Long, Long> getViews(List<Event> events);

    Map<Long, Long> getConfirmedRequests(List<Event> events);

}