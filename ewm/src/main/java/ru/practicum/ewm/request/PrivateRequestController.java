package ru.practicum.ewm.request;


import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.request.dto.ParticipationRequestDto;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{userId}/requests")
public class PrivateRequestController {

    private final RequestService requestService;

    @PostMapping
    public ResponseEntity<ParticipationRequestDto> create(@PathVariable Long userId, @RequestParam Long eventId) {
        return new ResponseEntity<>(requestService.create(userId, eventId), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<ParticipationRequestDto>> getRequestsByUserId(@PathVariable Long userId) {
        return new ResponseEntity<>(requestService.getRequestsByUserId(userId), HttpStatus.OK);
    }

    @PatchMapping("/{requestId}/cancel")
    public ResponseEntity<ParticipationRequestDto> cancel(@PathVariable Long userId, @PathVariable Long requestId) {
        return new ResponseEntity<>(requestService.cancel(userId, requestId), HttpStatus.OK);
    }

}
