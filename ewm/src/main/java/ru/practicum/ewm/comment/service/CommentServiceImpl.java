package ru.practicum.ewm.comment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.comment.model.Comment;
import ru.practicum.ewm.comment.dto.CommentMapper;
import ru.practicum.ewm.comment.dto.RequestCommentDto;
import ru.practicum.ewm.comment.dto.ResponseCommentDto;
import ru.practicum.ewm.comment.repository.CommentRepository;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.EventState;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.exceptions.CantDoException;
import ru.practicum.ewm.exceptions.NotFoundException;
import ru.practicum.ewm.user.User;
import ru.practicum.ewm.user.UserRepository;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final CommentMapper commentMapper;

    @Override
    @Transactional
    public ResponseCommentDto create(RequestCommentDto commentDto, Long userId, Long eventId) {
        Event event = checkEventExists(eventId);
        User user = checkUserExists(userId);
        checkEventIsPublished(event);
        Comment comment = commentRepository.save(commentMapper.commentDtotoComment(commentDto, user, event));

        return commentMapper.toResponseCommentDto(comment);
    }

    @Override
    public ResponseCommentDto getByUserById(Long userId, Long commentId) {
        return commentMapper.toResponseCommentDto(
                checkCommentOwner(userId, commentId));
    }

    @Override
    public List<ResponseCommentDto> getUsersComments(Long userId) {
        return commentMapper.toResponseCommentDto(
                commentRepository.getCommentsByAuthorId(userId));
    }

    @Override
    @Transactional
    public ResponseCommentDto update(RequestCommentDto commentDto, Long userId, Long commentId) {
        checkUserExists(userId);
        Comment comment = checkCommentOwner(userId, commentId);
        comment.setMessage(commentDto.getMessage());

        return commentMapper.toResponseCommentDto(commentRepository.save(comment));
    }

    @Override
    @Transactional
    public void deleteByUserById(Long userId, Long commentId) {
        checkCommentOwner(userId, commentId);
        commentRepository.deleteById(commentId);
    }

    @Override
    @Transactional
    public void deleteByAdminById(Long commentId) {
        checkCommentExists(commentId);
        commentRepository.deleteById(commentId);
    }

    @Override
    public List<ResponseCommentDto> getAllCommentsForEvent(Long eventId, String keyword, Integer from, Integer size) {
        return commentMapper.toResponseCommentDto(commentRepository
                .findAllCommentsForEvent(eventId, keyword, from, size));
    }

    @Override
    public ResponseCommentDto getByAdminById(Long commentId) {
        return commentMapper.toResponseCommentDto(
                checkCommentExists(commentId));
    }

    private Comment checkCommentExists(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException(Comment.class, commentId));
    }

    private User checkUserExists(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(User.class, userId));
    }

    private Comment checkCommentOwner(Long userId, Long commentId) {
        Comment comment = checkCommentExists(commentId);
        if (!comment.getAuthor().getId().equals(userId)) {
            throw new CantDoException("Запрос отклонен, нельзя взаимодействовать с чужим Comment");
        }
        return comment;
    }

    private Event checkEventExists(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(Event.class, eventId));
    }

    private void checkEventIsPublished(Event event) {
        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new CantDoException("Запрос отклонен, нельзя взаимодействовать с неопубликованным Event.");
        }
    }



}
