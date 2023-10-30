package ru.practicum.ewm.comment.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.comment.dto.ResponseCommentDto;
import ru.practicum.ewm.comment.service.CommentService;

@RestController
@Slf4j
@RequestMapping("/admin/comments")
@RequiredArgsConstructor
public class AdminCommentController {

    private final CommentService commentService;

    @GetMapping("/{commentId}")
    public ResponseEntity<ResponseCommentDto> getCommentById(@PathVariable Long commentId) {
        log.info("Получен запрос от администратора на получение Comment с id: " + commentId);
        return new ResponseEntity<>(commentService.getByAdminById(commentId), HttpStatus.OK);
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<?> deleteCommentById(@PathVariable Long commentId) {
        log.info("Получен запрос от администратора на удаление Comment с id: " + commentId);
        commentService.deleteByAdminById(commentId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
