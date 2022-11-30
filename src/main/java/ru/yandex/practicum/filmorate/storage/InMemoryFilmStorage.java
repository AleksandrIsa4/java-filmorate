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

    public void deleteFilm(Integer id) {
        films.remove(id);
    }

    public Film postFilm(Film film) {
        additionFilm(film);
        films.put(film.getId(), film);
        return films.get(film.getId());
    }

    public Film putFilm(Film film) {
        if (films.containsKey(film.getId())) {
            films.put(film.getId(), film);
            return films.get(film.getId());
        }
        return null;
    }

    public Collection<Film> getMemoryFilms() {
       return films.values();
    }

    public Film getFilmId(Integer id) {
        if (films.containsKey(id)) {
            return films.get(id);
        }
        return null;
    }

    private void additionFilm(Film film) {
        if (film.getId() == 0) {
            film.setId(++generator);
        }
    }

    public void addLikeUser(Integer id,Integer userId) {
        films.get(id).addLike(userId);
    }

    public void deleteLikeUser(Integer id,Integer userId) {
        films.get(id).deleteLike(userId);
    }

    public List<Film> countPopularFilm(Integer count) {
        List<Film> AllPopularFilm =films.values().stream().collect(
                Collectors.toCollection(ArrayList::new));
        AllPopularFilm.sort((Film o1, Film o2)->o2.getRate()-o1.getRate());
        while (AllPopularFilm.size()>count){
            AllPopularFilm.remove(AllPopularFilm.size()-1);
        }
        return AllPopularFilm;
    }
}
