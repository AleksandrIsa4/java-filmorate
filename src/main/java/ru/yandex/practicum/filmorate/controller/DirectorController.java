package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.BadRequestException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.DirectorService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/directors")
public class DirectorController {
    private final DirectorService directorService;

    @Autowired
    public DirectorController(DirectorService directorService) {
        this.directorService = directorService;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<Director> get() {
        return directorService.get();
    }

    @GetMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    public Director get(@PathVariable int id) {
        return directorService.get(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Director add(@RequestBody @Valid Director director) {
        if (director == null) {
            throw new BadRequestException("Bad request. Director couldn't be null.");
        }

        return directorService.add(director);
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public Director update(@RequestBody @Valid Director director) {
        if (director == null) {
            throw new BadRequestException("Bad request. Director couldn't be null.");
        }

        return directorService.update(director);
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    public Director delete(@PathVariable int id) {
        return (directorService.delete(id));
    }
}