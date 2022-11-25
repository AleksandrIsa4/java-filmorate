package ru.yandex.practicum.filmorate.validator.impl.utils;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

public class ValidatorTestUtils {

    public static User getUserRequest(String login, String email, String name, LocalDate localDate) {
        User userRequest = new User();
        userRequest.setLogin(login);
        userRequest.setEmail(email);
        userRequest.setBirthday(localDate);
        userRequest.setName(name);
        return userRequest;
    }

    public static Film getFilmRequest(String name, String description, LocalDate releaseDate, long duration) {
        Film filmRequest = new Film();
        filmRequest.setName(name);
        filmRequest.setDescription(description);
        filmRequest.setReleaseDate(releaseDate);
        filmRequest.setDuration(duration);
        return filmRequest;
    }
}
