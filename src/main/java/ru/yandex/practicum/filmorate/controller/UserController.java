package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.*;

@Validated
@RestController
@RequestMapping(
        value = "/users",
        consumes = MediaType.ALL_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
)
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public User userAdd(@RequestBody @Valid User user) {
        return userService.saveUser(user);
    }

    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> userUpdate(@RequestBody @Valid @NotNull User user) {
        User currentUser = userService.changeUser(user);
        if (currentUser == null) {
            Map<String, Object> body = new LinkedHashMap<>();
            body.put("Запись не найдена с id ", user.getId());
            body.put("Код ошибки", HttpStatus.NOT_FOUND.value());
            return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(currentUser, HttpStatus.OK);
    }

    @GetMapping
    public Collection<User> userAll() {
        return userService.getUsers();
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> userId(@PathVariable("id") @NotNull Integer id) {
        User currentUser = userService.getUser(id);
        if (currentUser == null) {
            Map<String, Object> body = new LinkedHashMap<>();
            body.put("Запись не найдена с id ", id);
            body.put("Код ошибки", HttpStatus.NOT_FOUND.value());
            return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(currentUser, HttpStatus.OK);
    }

    @PutMapping(value = "/{id}/friends/{friendId}")
    public ResponseEntity<?> userFriendsUpdate(@PathVariable("id") @NotNull Integer id, @PathVariable("friendId") @NotNull Integer friendId) {
        if (id == friendId) {
            Map<String, Object> body = new LinkedHashMap<>();
            body.put("Запись повторяется c id ", id);
            body.put("Код ошибки", HttpStatus.BAD_REQUEST.value());
            return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
        }
        // Если idBody не Null, значит один из пользователей с Id не найден
        Integer idBody = userService.changeFriend(id, friendId);
        if (idBody != null) {
            Map<String, Object> body = new LinkedHashMap<>();
            body.put("Запись не найдена с id ", idBody);
            body.put("Код ошибки", HttpStatus.NOT_FOUND.value());
            return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>(userService.getUser(id), HttpStatus.OK);
        }
    }

    @DeleteMapping(value = "/{id}/friends/{friendId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> userFriendsDelete(@PathVariable("id") @NotNull Integer id, @PathVariable("friendId") @NotNull Integer friendId) {
        if (id == friendId) {
            Map<String, Object> body = new LinkedHashMap<>();
            body.put("Запись повторяется c id ", id);
            body.put("Код ошибки", HttpStatus.BAD_REQUEST.value());
            return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
        }
        // Если idBody не Null, значит один из пользователей с Id не найден
        Integer idBody = userService.deleteFriend(id, friendId);
        if (idBody != null) {
            Map<String, Object> body = new LinkedHashMap<>();
            body.put("Запись не найдена с id ", idBody);
            body.put("Код ошибки", HttpStatus.NOT_FOUND.value());
            return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>(userService.getUser(id), HttpStatus.OK);
        }
    }

    @GetMapping(value = "/{id}/friends", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> userIdFriend(@PathVariable("id") @NotNull Integer id) {
        List<User> friends = userService.getUserFriend(id);
        if (friends == null) {
            Map<String, Object> body = new LinkedHashMap<>();
            body.put("Запись не найдена с id ", id);
            body.put("Код ошибки", HttpStatus.NOT_FOUND.value());
            return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(friends, HttpStatus.OK);
    }

    @GetMapping(value = "/{id}/friends/common/{otherId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> usersCommonFriend(@PathVariable("id") @NotNull Integer id, @PathVariable("otherId") @NotNull Integer otherId) {
        List<User> friends = userService.getCommonFriend(id, otherId);
        if (friends == null) {
            Map<String, Object> body = new LinkedHashMap<>();
            body.put("Возможно не найдена запись id User ", id);
            body.put("Возможно не найдена запись id OtherUser ", otherId);
            body.put("Код ошибки", HttpStatus.NOT_FOUND.value());
            return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(friends, HttpStatus.OK);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Integer> userDelete(@PathVariable("id") @NotNull Integer id) {
        userService.deleteUser(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping(value = "/{id}/feed")
    public ResponseEntity<?> findFeed(@PathVariable @NotNull Integer id) {
        List<Event> feed = userService.getFeed(id);
        if (feed == null) {
            Map<String, Object> body = new LinkedHashMap<>();
            body.put("Запись не найдена с id ", id);
            body.put("Код ошибки", HttpStatus.NOT_FOUND.value());
            return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(feed,HttpStatus.OK);
    }

    @GetMapping(value = "/{id}/recommendations", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> userIdRecomment(@PathVariable("id") @NotNull Integer id) {
        List<Film> films = userService.getUserRecomment(id);
        if (films == null) {
            Map<String, Object> body = new LinkedHashMap<>();
            body.put("Запись не найдена с id ", id);
            body.put("Код ошибки", HttpStatus.NOT_FOUND.value());
            return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(films, HttpStatus.OK);
    }
}
