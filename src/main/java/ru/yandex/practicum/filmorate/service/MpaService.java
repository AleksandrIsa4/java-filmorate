package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class MpaService {

    private final MpaStorage mpaStorage;

    public Collection<Rating> getMpa() {
        return mpaStorage.getAllMpa();
    }

    public Rating getMpaId(Integer id) {
        Rating rating = mpaStorage.getMpaId(id);
        if (rating == null) {
            throw new NotFoundException(HttpStatus.NOT_FOUND, "rating id not found");
        }
        return rating;
    }
}
