package ru.yandex.practicum.filmorate.exceptions;

import org.springframework.http.HttpStatus;

public class NotFoundException extends RuntimeException{

    public NotFoundException(HttpStatus status, String str) {
        super(str);
    }
}