package ru.practicum.ewm.comment.service;


import ru.practicum.ewm.comment.dto.RequestCommentDto;
import ru.practicum.ewm.comment.dto.ResponseCommentDto;

import java.util.List;

public interface CommentService {

    ResponseCommentDto create(RequestCommentDto commentDto, Long userId, Long eventId);

    ResponseCommentDto getByUserById(Long userId, Long commentId);

    List<ResponseCommentDto> getUsersComments(Long userId);

    ResponseCommentDto update(RequestCommentDto commentDto, Long userId, Long commentId);

    void deleteByUserById(Long userId, Long commentId);

    void deleteByAdminById(Long commentId);

    List<ResponseCommentDto> getAllCommentsForEvent(Long eventId, String keyword, Integer from, Integer size);

    ResponseCommentDto getByAdminById(Long commentId);

}
