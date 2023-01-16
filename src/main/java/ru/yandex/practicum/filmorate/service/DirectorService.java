package ru.yandex.practicum.filmorate.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.director.DirectorStorage;

import java.util.List;
import java.util.Map;

@Service
public class DirectorService {
    private final Logger log = LoggerFactory.getLogger(DirectorService.class);
    private final DirectorStorage directorStorage;

    @Autowired
    public DirectorService(DirectorStorage directorStorage) {
        this.directorStorage = directorStorage;
    }

    public List<Director> get() {
        log.info("get all director --OK");

        return directorStorage.findAll();
    }

    public Director get(int id) {
        log.info("get director by id --OK");

        return directorStorage.find(id)
                .orElseThrow(() -> new NotFoundException(HttpStatus.NOT_FOUND, "director id not found"));
    }

    public Director add(Director director) {
        log.info("director add --OK");

        return directorStorage.add(director);
    }

    public Director update(Director director) {
        throwIfDirectorNotValid(director.getId());
        log.info("director update --OK");

        return directorStorage.update(director);
    }

    public Director delete(int id) {
        throwIfDirectorNotValid(id);
        log.info("director delete --OK");

        return directorStorage.delete(id);
    }

    public void addToFilm(int filmId, int directorId) {
        throwIfDirectorNotValid(directorId);
        log.info("add director to film --OK");

        directorStorage.addToFilm(filmId, directorId);
    }

    public void deleteAllByFilmId(int filmId) {
        log.info("delete director from film --OK");

        directorStorage.deleteAllByFilmId(filmId);
    }

    public List<Director> findAllToFilm(int filmId) {
        log.info("find directors for film --OK");

        return directorStorage.findAllToFilm(filmId);
    }

    public Map<Integer, List<Director>> findAllToFilm(){
        log.info("find all directors for films --OK");

        return directorStorage.findAllToFilm();
    }

    private void throwIfDirectorNotValid(int id) {
        if (!directorStorage.isAlreadyExist(id)) {
            log.info("director id check --FAIL");
            throw new NotFoundException(HttpStatus.NOT_FOUND, "director id not found");
        }
    }
}