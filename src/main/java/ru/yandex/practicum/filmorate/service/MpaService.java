package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.util.Collection;

@Service("mpaService")
@RequiredArgsConstructor
public class MpaService {

    @Autowired
    private final MpaStorage mpaStorage;

    public Collection<Rating> getMpa() {
        return mpaStorage.getAllMpa();
    }

    public Rating getMpaId(Integer id) {
        return mpaStorage.getMpaId(id);
    }
}
