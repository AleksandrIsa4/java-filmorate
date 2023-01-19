package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class InMemoryFilmStorage implements FilmStorage {

    private int generator = 0;
    private final Map<Integer, Film> films = new HashMap<>();

    @Override
    public void deleteFilm(Integer id) {
        films.remove(id);
    }

    @Override
    public Film postFilm(Film film) {
        additionFilm(film);
        films.put(film.getId(), film);
        return films.get(film.getId());
    }

    @Override
    public Film putFilm(Film film) {
        if (films.containsKey(film.getId())) {
            films.put(film.getId(), film);
            return films.get(film.getId());
        }
        return null;
    }

    @Override
    public Collection<Film> getMemoryFilms() {
        return films.values();
    }

    @Override
    public Film getFilmId(Integer id) {
        if (films.containsKey(id)) {
            return films.get(id);
        }
        return null;
    }

    @Override
    public void addLikeUser(Integer id, Integer userId) {
        Set<Integer> users = films.get(id).getLike();
        users.add(userId);
        films.get(id).setLike(users);
        films.get(id).setRate(films.get(id).getRate() + 1);
    }

    @Override
    public void deleteLikeUser(Integer id, Integer userId) {
        Set<Integer> users = films.get(id).getLike();
        users.remove(userId);
        films.get(id).setLike(users);
        films.get(id).setRate(films.get(id).getRate() - 1);
    }

    @Override
    public List<Film> countPopularFilm(Integer count) {
        List<Film> AllPopularFilm = films.values().stream().collect(
                Collectors.toCollection(ArrayList::new));
        AllPopularFilm.sort((Film o1, Film o2) -> o2.getRate() - o1.getRate());
        while (AllPopularFilm.size() > count) {
            AllPopularFilm.remove(AllPopularFilm.size() - 1);
        }
        return AllPopularFilm;
    }

    @Override
    public List<Film> getFilmsByDirector(int directorId, String sortType) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Collection<Film> searchFilmByQuery(String query, String[] by) { throw new UnsupportedOperationException(); }

    @Override
    public Collection<Film> searchCommonFilm(String userId, String friendId) { throw new UnsupportedOperationException(); }

    private void additionFilm(Film film) {
        if (film.getId() == 0) {
            film.setId(++generator);
        }
    }
}
