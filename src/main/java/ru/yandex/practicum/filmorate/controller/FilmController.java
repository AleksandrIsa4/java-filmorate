package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.BadRequestException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FeedService;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.*;

@Validated
@RestController
@RequestMapping(
        value = "/films",
        consumes = MediaType.ALL_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
)
@RequiredArgsConstructor
public class FilmController {

    private final FilmService filmService;
    private final FeedService feedService;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public Film filmAdd(@RequestBody @Valid @NotNull Film film) {
        if (film == null) {
            throw new BadRequestException("Bad request. Film couldn't be null.");
        }
        return filmService.saveFilm(film);
    }

    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public Film filmUpdate(@RequestBody @Valid @NotNull Film film) {
        if (film == null) {
            throw new BadRequestException("Bad request. Film couldn't be null.");
        }
        return filmService.changeFilm(film);
    }

    @GetMapping
    public Collection<Film> filmAll() {
        return filmService.getFilms();
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Film filmId(@PathVariable("id") @NotNull Integer id) {
        return filmService.getFilm(id);
    }

    @GetMapping(value = "/search")
    public Collection<Film> search(@RequestParam String query, @RequestParam String by) {
        String queryLower = query.toLowerCase();
        String[] byMassiv = by.split(",");
        return filmService.searchFilmByQuery(queryLower, byMassiv);
    }

    @GetMapping(value = "/common")
    public Collection<Film> searchCommon(@RequestParam String userId, @RequestParam String friendId) {
        return filmService.searchCommonFilm(userId, friendId);
    }

    @PutMapping(value = "/{id}/like/{userId}")
    public Film userLikeAdd(@PathVariable("id") @NotNull Integer id, @PathVariable("userId") @NotNull Integer userId) {
        filmService.changeLike(id, userId);
        feedService.createLikeAddition(userId, id);
        return filmService.getFilm(id);
    }

    @DeleteMapping(value = "/{id}/like/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Film userLikeDelete(@PathVariable("id") @NotNull Integer id, @PathVariable("userId") @NotNull Integer userId) {
        filmService.deleteLike(id, userId);
        feedService.createLikeDeletion(userId, id);
        return filmService.getFilm(id);
    }

    @GetMapping(value = "/popular", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Film> filmPopular(@RequestParam(defaultValue = "10") Integer count,
                                  @RequestParam(required = false) Integer genreId,
                                  @RequestParam(required = false) Integer year) {
        return filmService.popularFilm(count, genreId, year);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Integer> filmDelete(@PathVariable("id") @NotNull Integer id) {
        filmService.deleteFilm(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping(value = "/director/{directorId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Film> getFilmsByDirectorId(@PathVariable int directorId,
                                           @RequestParam String sortBy) {
        return filmService.getFilmsByDirector(directorId, sortBy);
    }
}
