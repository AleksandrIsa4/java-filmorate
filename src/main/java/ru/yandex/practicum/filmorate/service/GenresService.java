package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
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
        Genre genre = genreStorage.getGenreId(id);
        if (genre == null) {
            throw new NotFoundException(HttpStatus.NOT_FOUND, "genre id not found");
        }
        return genre;
    }
}
