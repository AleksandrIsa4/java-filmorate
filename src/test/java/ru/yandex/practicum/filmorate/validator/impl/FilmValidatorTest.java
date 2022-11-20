package ru.yandex.practicum.filmorate.validator.impl;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

import static ru.yandex.practicum.filmorate.validator.impl.utils.ValidatorTestUtils.getFilmRequest;
import static ru.yandex.practicum.filmorate.validator.utils.ValidatorTestUtils.dtoHasErrorMessage;

public class FilmValidatorTest {

    @Test
    void createUserFailName() {
        Film film = getFilmRequest("", "adipisicing", LocalDate.parse("1967-03-25"), 100);
        Assertions.assertTrue(dtoHasErrorMessage(film, " Film не может быть пустым"));
    }

    @Test
    void createUserFailDescription() {
        Film film = getFilmRequest("nisi eiusmod", "Пятеро друзей ( комик-группа «Шарло»), приезжают в город Бризуль. Здесь они хотят разыскать господина Огюста Куглова, который задолжал им деньги, а именно 20 миллионов. о Куглов, который за время «своего отсутствия», стал кандидатом Коломбани.", LocalDate.parse("1967-03-25"), 100);
        Assertions.assertTrue(dtoHasErrorMessage(film, " Film не может быть длиннее 200 символов"));
    }

    @Test
    void createUserFailReleaseDate() {
        Film film = getFilmRequest("nisi eiusmod", "adipisicing", LocalDate.parse("1767-03-25"), 100);
        Assertions.assertTrue(dtoHasErrorMessage(film, "releaseDate Film не может быть раньше 28 декабря 1895 года"));
    }

    @Test
    void createUserFailDuration() {
        Film film = getFilmRequest("nisi eiusmod", "adipisicing", LocalDate.parse("1967-03-25"), -100);
        Assertions.assertTrue(dtoHasErrorMessage(film, " Film не может быть отрицательным"));
    }
}
