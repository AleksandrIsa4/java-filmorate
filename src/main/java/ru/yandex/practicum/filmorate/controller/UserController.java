package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@Validated
@RestController
@RequestMapping(
        value = "/users",
        consumes = MediaType.ALL_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
)

public class UserController {

    private int generator = 0;
    private final Map<Integer, User> users = new HashMap<>();

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public User userAdd(@RequestBody @Valid User user) {
        additionUser(user);
        users.put(user.getId(), user);
        log.info("Получен POST User");
        return user;
    }

    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> userUpdate(@RequestBody @Valid @NotNull User user) {
        if (users.containsKey(user.getId())) {
            users.put(user.getId(), user);
            log.info("Получен PUT User");
            return new ResponseEntity<>(user, HttpStatus.OK);
        }
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("Запись не найдена с id ", user.getId());
        body.put("Код ошибки", HttpStatus.NOT_FOUND.value());
        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }

    @GetMapping
    public Collection<User> userAll() {
        return users.values();
    }

    private void additionUser(User user) {
        if (user.getName() == null) {
            user.setName(user.getLogin());
        }
        if (user.getId() == 0) {
            user.setId(++generator);
        }
    }
}
