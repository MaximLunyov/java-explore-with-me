package ru.practicum.ewm.exceptions;

public class CantDoException extends RuntimeException {

    public CantDoException(String message) {
        super(message);
    }

}