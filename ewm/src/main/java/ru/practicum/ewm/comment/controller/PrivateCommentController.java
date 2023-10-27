package ru.practicum.ewm.comment.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewm.comment.dto.RequestCommentDto;
import ru.practicum.ewm.comment.dto.ResponseCommentDto;
import ru.practicum.ewm.comment.service.CommentService;

import javax.validation.Valid;
import java.util.List;

@Validated
@RestController
@Slf4j
@RequestMapping("/users/{userId}/comments")
@RequiredArgsConstructor
public class PrivateCommentController {

    private final CommentService commentService;

    @PostMapping("/{eventId}")
    public ResponseEntity<ResponseCommentDto> createComment(@RequestBody @Valid RequestCommentDto commentDto,
                                                            @PathVariable Long userId, @PathVariable Long eventId) {
        log.info("Получен запрос на создание Comment на Event с ID {} от User c ID {}.", eventId, userId);
        return new ResponseEntity<>(commentService.create(commentDto, userId, eventId), HttpStatus.CREATED);
    }

    @GetMapping("/{commentId}")
    public ResponseEntity<ResponseCommentDto> getCommentById(@PathVariable Long userId,
                                                             @PathVariable Long commentId) {
        log.info("Получен запрос на получение Comment c ID {}, созданный User c ID {}.", commentId, userId);
        return new ResponseEntity<>(commentService.getByUserById(userId, commentId), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<ResponseCommentDto>> getListComments(@PathVariable Long userId) {
        log.info("Получен запрос на получение списка всех Comments, созданных User c ID {}.", userId);
        return new ResponseEntity<>(commentService.getUsersComments(userId), HttpStatus.OK);
    }

    @PatchMapping("/{commentId}")
    public ResponseEntity<ResponseCommentDto> updateComment(@RequestBody @Valid RequestCommentDto commentDto,
                                                            @PathVariable Long userId, @PathVariable Long commentId) {
        log.info("Получен запрос на обновление Comment c ID {} от User c ID {}", commentId, userId);
        return new ResponseEntity<>(commentService.update(commentDto, userId, commentId), HttpStatus.OK);
    }

    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<?> deleteCommentById(@PathVariable Long userId, @PathVariable Long commentId) {
        commentService.deleteByUserById(userId, commentId);
        log.info("Получен запрос на удаление Comment c ID {} от User c ID {}.", commentId, userId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}