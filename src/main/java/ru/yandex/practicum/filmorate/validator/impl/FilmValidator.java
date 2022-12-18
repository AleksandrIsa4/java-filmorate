package ru.yandex.practicum.filmorate.validator.impl;

import ru.yandex.practicum.filmorate.validator.FilmValid;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;

public class FilmValidator implements ConstraintValidator<FilmValid, LocalDate> {
    private static final LocalDate BEGIN_MOVIE = LocalDate.of(1895, 12, 28);
    boolean isDate;

    @Override
    public boolean isValid(LocalDate releaseDate, ConstraintValidatorContext context) {
        isDate = releaseDate.isAfter(LocalDate.of(1895, 12, 28));
        if (isDate) {
            return true;
        } else {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("releaseDate Film не может быть раньше 28 декабря 1895 года")
                    .addConstraintViolation();
            return false;
        }
    }
}
