package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    @Autowired
    @Qualifier("userDbStorage")
    private final UserStorage inMemoryUserStorage;

    public void deleteUser(Integer id) {
        log.info("Получен DELETE User");
        inMemoryUserStorage.deleteUser(id);
    }

    public User saveUser(User user) {
        log.info("Получен POST User");
        return inMemoryUserStorage.postUser(user);
    }

    public User changeUser(User user) {
        if (inMemoryUserStorage.putUser(user) == null) {
            throw new NotFoundException(HttpStatus.NOT_FOUND, "user id not found");
        }
        log.info("Получен PUT User");
        return inMemoryUserStorage.putUser(user);
    }

    public Collection<User> getUsers() {
        return inMemoryUserStorage.getMemoryUsers();
    }

    public User getUser(Integer id) {
        if (inMemoryUserStorage.getUserId(id) == null) {
            throw new NotFoundException(HttpStatus.NOT_FOUND, "user id not found");
        }
        return inMemoryUserStorage.getUserId(id);
    }

    public void changeFriend(Integer id, Integer friendId) {
        if (getUser(id) == null || getUser(friendId) == null) {
            throw new NotFoundException(HttpStatus.NOT_FOUND, "user id not found");
        }
        inMemoryUserStorage.addFriendId(id, friendId);
        log.info("Получен друг PUT User");
    }

    public void deleteFriend(Integer id, Integer friendId) {
        if (getUser(id) == null || getUser(friendId) == null) {
            throw new NotFoundException(HttpStatus.NOT_FOUND, "user id not found");
        }
        inMemoryUserStorage.deleteFriendId(id, friendId);
    }

    public List<User> getUserFriend(Integer id) {
        if (getUser(id) == null) {
            throw new NotFoundException(HttpStatus.NOT_FOUND, "user id not found");
        }
        return inMemoryUserStorage.getUserIdFriend(id);
    }

    public List<User> getCommonFriend(Integer id, Integer otherId) {
        if (getUser(id) == null || getUser(otherId) == null) {
            throw new NotFoundException(HttpStatus.NOT_FOUND, "user id not found");
        }
        return inMemoryUserStorage.getUsersCommonFriends(id, otherId);
    }

    public List<Film> getUserRecomment(Integer id) {
        if (getUser(id) == null) {
            throw new NotFoundException(HttpStatus.NOT_FOUND, "user id not found");
        }
        return inMemoryUserStorage.getUserIdRecomment(id);
    }
}
