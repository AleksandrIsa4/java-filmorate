package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.Collection;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmService {

    @Autowired
    @Qualifier("filmDbStorage")
    private final FilmStorage inMemoryFilmStorage;
    private final UserService userService;

    public Film saveFilm(Film film) {
        log.info("Получен POST Film");
        return inMemoryFilmStorage.postFilm(film);
    }

    public Film changeFilm(Film film) {
        if (inMemoryFilmStorage.putFilm(film) == null) {
            return null;
        }
        log.info("Получен PUT Film");
        return inMemoryFilmStorage.putFilm(film);
    }

    public Collection<Film> getFilms() {
        return inMemoryFilmStorage.getMemoryFilms();
    }

    public Film getFilm(Integer id) {
        if (inMemoryFilmStorage.getFilmId(id) == null) {
            return null;
        }
        return inMemoryFilmStorage.getFilmId(id);
    }

    public Integer changeLike(Integer id, Integer userId) {
        if (getFilm(id) == null) {
            return id;
        }
        if (userService.getUser(userId) == null) {
            return userId;
        }
        inMemoryFilmStorage.addLikeUser(id, userId);
        log.info("Получен лайк PUT Film");
        return null;
    }

    public Integer deleteLike(Integer id, Integer userId) {
        if (getFilm(id) == null) {
            return id;
        }
        if (userService.getUser(userId) == null) {
            return userId;
        }
        inMemoryFilmStorage.deleteLikeUser(id, userId);
        return null;
    }

    public List<Film> popularFilm(Integer count) {
        if (count == null) {
            return inMemoryFilmStorage.countPopularFilm(10);
        } else {
            return inMemoryFilmStorage.countPopularFilm(count);
        }
    }
}
