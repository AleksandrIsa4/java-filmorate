package ru.yandex.practicum.filmorate.storage.director;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface DirectorStorage {

    List<Director> findAll();

    Optional<Director> find(int id);

    Director add(Director director);

    Director update(Director director);

    Director delete(int id);

    void addToFilm(int filmId, int directorId);

    void deleteAllByFilmId(int filmId);

    List<Director> findAllToFilm(int filmId);

    Map<Integer, List<Director>> findAllToFilm();

    boolean isAlreadyExist(int id);
}
