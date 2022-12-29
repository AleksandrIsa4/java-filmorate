package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
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
public class FilmController {

    private final FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public Film filmAdd(@RequestBody @Valid @NotNull Film film) {
        return filmService.saveFilm(film);
    }

    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> filmUpdate(@RequestBody @Valid @NotNull Film film) {
        Film currentFilm = filmService.changeFilm(film);
        if (currentFilm == null) {
            Map<String, Object> body = new LinkedHashMap<>();
            body.put("Запись не найдена с id ", film.getId());
            body.put("Код ошибки", HttpStatus.NOT_FOUND.value());
            return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(currentFilm, HttpStatus.OK);
    }

    @GetMapping
    public Collection<Film> filmAll() {
        return filmService.getFilms();
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> filmId(@PathVariable("id") @NotNull Integer id) {
        Film currentFilm = filmService.getFilm(id);
        if (currentFilm == null) {
            Map<String, Object> body = new LinkedHashMap<>();
            body.put("Запись не найдена с id ", id);
            body.put("Код ошибки", HttpStatus.NOT_FOUND.value());
            return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(currentFilm, HttpStatus.OK);
    }

    @PutMapping(value = "/{id}/like/{userId}")
    public ResponseEntity<?> userLikeAdd(@PathVariable("id") @NotNull Integer id, @PathVariable("userId") @NotNull Integer userId) {
        // Если idBody не Null, значит один из Id не найден
        Integer idBody = filmService.changeLike(id, userId);
        if (idBody != null) {
            Map<String, Object> body = new LinkedHashMap<>();
            body.put("Запись не найдена с id ", idBody);
            body.put("Код ошибки", HttpStatus.NOT_FOUND.value());
            return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>(filmService.getFilm(id), HttpStatus.OK);
        }
    }

    @DeleteMapping(value = "/{id}/like/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> userLikeDelete(@PathVariable("id") @NotNull Integer id, @PathVariable("userId") @NotNull Integer userId) {
        // Если idBody не Null, значит один из пользователей с Id не найден
        Integer idBody = filmService.deleteLike(id, userId);
        if (idBody != null) {
            Map<String, Object> body = new LinkedHashMap<>();
            body.put("Запись не найдена с id ", idBody);
            body.put("Код ошибки", HttpStatus.NOT_FOUND.value());
            return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>(filmService.getFilm(id), HttpStatus.OK);
        }
    }

    @GetMapping(value = "/popular", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> filmPopular(@RequestParam(required = false) Integer count) {
        List<Film> popularFilms = filmService.popularFilm(count);
        return new ResponseEntity<>(popularFilms, HttpStatus.OK);
    }
}
