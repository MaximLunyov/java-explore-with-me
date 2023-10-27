package ru.practicum.ewm.exceptions;

public class NotFoundException extends RuntimeException {

    public NotFoundException(Class<?> entityClass, Long entityId) {
        super(entityClass.getSimpleName() + " c ID = " + entityId + " не найден.");
    }

    public NotFoundException(String message) {
        super(message);
    }
}
