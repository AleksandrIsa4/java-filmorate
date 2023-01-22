package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.BadRequestException;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FeedService;
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
    private final FeedService feedService;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public User userAdd(@RequestBody @Valid User user) {
        return userService.saveUser(user);
    }

    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public User userUpdate(@RequestBody @Valid @NotNull User user) {
        if (user == null) {
            throw new BadRequestException("Bad request. User couldn't be null.");
        }
        return userService.changeUser(user);
    }

    @GetMapping
    public Collection<User> userAll() {
        return userService.getUsers();
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public User userId(@PathVariable("id") @NotNull Integer id) {
        return userService.getUser(id);
    }

    @PutMapping(value = "/{id}/friends/{friendId}")
    public User userFriendsUpdate(@PathVariable("id") @NotNull Integer id, @PathVariable("friendId") @NotNull Integer friendId) {
        if (id == friendId) {
            throw new BadRequestException("Bad request. Film couldn't be null.");
        }
        userService.changeFriend(id, friendId);
        feedService.createFriendAddition(id, friendId);
        return userService.getUser(id);
    }

    @DeleteMapping(value = "/{id}/friends/{friendId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public User userFriendsDelete(@PathVariable("id") @NotNull Integer id, @PathVariable("friendId") @NotNull Integer friendId) {
        if (id == friendId) {
            throw new BadRequestException("Bad request. Film couldn't be null.");
        }
        userService.deleteFriend(id, friendId);
        feedService.createFriendDeletion(id, friendId);
        return userService.getUser(id);
    }

    @GetMapping(value = "/{id}/friends", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<User> userIdFriend(@PathVariable("id") @NotNull Integer id) {
        return userService.getUserFriend(id);
    }

    @GetMapping(value = "/{id}/friends/common/{otherId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<User> usersCommonFriend(@PathVariable("id") @NotNull Integer id, @PathVariable("otherId") @NotNull Integer otherId) {
        return userService.getCommonFriend(id, otherId);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Integer> userDelete(@PathVariable("id") @NotNull Integer id) {
        userService.deleteUser(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping(value = "/{id}/feed")
    public List<Event> findFeed(@PathVariable @NotNull Integer id) {
        return feedService.getFeed(id);
    }

    @GetMapping(value = "/{id}/recommendations", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Film> userIdRecomment(@PathVariable("id") @NotNull Integer id) {
        return userService.getUserRecomment(id);
    }
}
