package ru.practicum.ewm.comment.repository;


import ru.practicum.ewm.comment.model.Comment;

import java.util.List;

public interface CustomCommentRepository {

    List<Comment> findAllCommentsForEvent(Long eventId, String keyword, Integer from, Integer size);

}