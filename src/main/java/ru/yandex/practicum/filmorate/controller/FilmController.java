package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.*;

@Slf4j
@Validated
@RestController
@RequestMapping(
        value = "/films",
        consumes = MediaType.ALL_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
)
public class FilmController {

    private int generator = 0;
    private final Map<Integer, Film> films = new HashMap<>();

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public Film filmAdd(@RequestBody @Valid @NotNull Film film) {
        additionFilm(film);
        films.put(film.getId(), film);
        log.info("Получен POST Film");
        return film;
    }

    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> filmUpdate(@RequestBody @Valid @NotNull Film film) {
        if (films.containsKey(film.getId())) {
            films.put(film.getId(), film);
            log.info("Получен PUT Film");
            return new ResponseEntity<>(film, HttpStatus.OK);
        }
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("Запись не найдена с id ", film.getId());
        body.put("Код ошибки", HttpStatus.NOT_FOUND.value());
        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }

    @GetMapping
    public Collection<Film> filmAll() {
        return films.values();
    }

    private void additionFilm(Film film) {
        if (film.getId() == 0) {
            film.setId(++generator);
        }
    }
}
