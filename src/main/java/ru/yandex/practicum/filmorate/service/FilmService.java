package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FeedDbStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmService {

    @Autowired
    @Qualifier("filmDbStorage")
    private final FilmStorage inMemoryFilmStorage;
    private final UserService userService;
    private final DirectorService directorService;
    private final FeedDbStorage feedDbStorage;

    public void deleteFilm(Integer id) {
        log.info("Получен DELETE Film");
        directorService.deleteAllByFilmId(id);
        inMemoryFilmStorage.deleteFilm(id);
    }

    public Film saveFilm(Film film) {
        log.info("Получен POST Film");
        Film film1 = inMemoryFilmStorage.postFilm(film);
        film1.setDirectors(directorService.findAllToFilm(film1.getId()));

        return film1;
    }

    public Film changeFilm(Film film) {
        if (inMemoryFilmStorage.putFilm(film) == null) {
            return null;
        }
        log.info("Получен PUT Film");
        if (film.getDirectors() != null) {
            for (Director director : film.getDirectors()) {
                directorService.addToFilm(film.getId(), director.getId());
            }
        } else {
            directorService.deleteAllByFilmId(film.getId());
        }
        Film film1 = inMemoryFilmStorage.putFilm(film);
        film1.setDirectors(directorService.findAllToFilm(film1.getId()));

        return film1;
    }

    public Collection<Film> getFilms() {
        Collection<Film> films = inMemoryFilmStorage.getMemoryFilms();

        return addDirectors(films);
    }

    public Collection<Film> searchFilmByQuery(String query, String[] by) {

        return inMemoryFilmStorage.searchFilmByQuery(query, by);
    }

    public Collection<Film> searchCommonFilm(String userId, String friendId) {

        return inMemoryFilmStorage.searchCommonFilm(userId, friendId);
    }

    public Film getFilm(Integer id) {
        if (inMemoryFilmStorage.getFilmId(id) == null) {
            return null;
        }
        Film film = inMemoryFilmStorage.getFilmId(id);
        film.setDirectors(directorService.findAllToFilm(film.getId()));

        return film;
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
        feedDbStorage.createLikeAddition(userId, id);
        log.info("Добавлен like_add_event в таблицу feed");
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
        feedDbStorage.createLikeDeletion(userId, id);
        log.info("Добавлен like_delete_event в таблицу feed");
        return null;
    }

    public List<Film> popularFilm(Integer count) {
        if (count == null) {
            return inMemoryFilmStorage.countPopularFilm(10);
        } else {
            return inMemoryFilmStorage.countPopularFilm(count);
        }
    }

    public List<Film> getFilmsByDirector(int directorId, String sortType) {
        directorService.get(directorId);
        List<Film> films = inMemoryFilmStorage.getFilmsByDirector(directorId, sortType);
        log.info("get films by director (sorted by " + sortType + ") --OK");

        return addDirectors(films);
    }

    private List<Film> addDirectors(Collection<Film> films) {
        Map<Integer, List<Director>> directorsToFilms = directorService.findAllToFilm();
        for (Film film : films) {
            if (directorsToFilms.containsKey(film.getId())) {
                film.setDirectors(directorsToFilms.get(film.getId()));
            } else {
                film.setDirectors(new ArrayList<>());
            }
        }
        return new ArrayList<>(films);
    }
}
