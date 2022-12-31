package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenresStorage;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class GenresService {

    private final GenresStorage genreStorage;

    public Collection<Genre> getGenre() {
        return genreStorage.getAllGenres();
    }

    public Genre getGenreId(Integer id) {
        return genreStorage.getGenreId(id);
    }
}
