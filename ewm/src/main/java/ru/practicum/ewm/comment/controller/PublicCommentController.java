package ru.practicum.ewm.comment.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.comment.dto.ResponseCommentDto;
import ru.practicum.ewm.comment.service.CommentService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Validated
@RestController
@Slf4j
@RequestMapping("/comments/{eventId}")
@RequiredArgsConstructor
public class PublicCommentController {

    private final CommentService commentService;

    @GetMapping
    public ResponseEntity<List<ResponseCommentDto>> getAllCommentsForEvent(@PathVariable Long eventId,
                                                                           @RequestParam(required = false) String keyword,
                                                                           @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                                                           @RequestParam(defaultValue = "10") @Positive Integer size) {
        log.info("Получен запрос на получение списка всех Comments для Event c ID {}.",
                eventId);
        return new ResponseEntity<>(commentService.getAllCommentsForEvent(eventId, keyword, from, size), HttpStatus.OK);
    }

}
