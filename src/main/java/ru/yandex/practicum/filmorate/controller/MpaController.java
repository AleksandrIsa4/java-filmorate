package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.service.MpaService;

import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

@Validated
@RestController
@RequestMapping(
        value = "/mpa",
        consumes = MediaType.ALL_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
)
@RequiredArgsConstructor
public class MpaController {

    private final MpaService mpaService;

    @GetMapping
    public Collection<Rating> mpaAll() {
        return mpaService.getMpa();
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> mpaId(@PathVariable("id") @NotNull Integer id) {
        Rating currentRating = mpaService.getMpaId(id);
        if (currentRating == null) {
            Map<String, Object> body = new LinkedHashMap<>();
            body.put("Запись не найдена с id ", id);
            body.put("Код ошибки", HttpStatus.NOT_FOUND.value());
            return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(currentRating, HttpStatus.OK);
    }
}
