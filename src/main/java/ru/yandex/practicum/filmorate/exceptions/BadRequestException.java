package ru.yandex.practicum.filmorate.exceptions;

public class BadRequestException extends RuntimeException {

    public BadRequestException(String str) {
        super(str);
    }
}
