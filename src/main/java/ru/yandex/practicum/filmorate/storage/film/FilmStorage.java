package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.List;

public interface FilmStorage {

    public void deleteFilm(Integer id);

    public Film postFilm(Film film);

    public Film putFilm(Film film);

    public Collection<Film> getMemoryFilms();

    public Film getFilmId(Integer id);

    public void addLikeUser(Integer id, Integer userId);

    public void deleteLikeUser(Integer id, Integer userId);

    public List<Film> countPopularFilm(Integer count, Integer genreId, Integer year);

    List<Film> getFilmsByDirector(int directorId, String sortType);

    Collection<Film> searchFilmByQuery(String query, String[] by);
}
