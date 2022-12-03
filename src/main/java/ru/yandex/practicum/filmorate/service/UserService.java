package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserStorage inMemoryUserStorage;

    public User saveUser(User user) {
        log.info("Получен POST User");
        return inMemoryUserStorage.postUser(user);
    }

    public User changeUser(User user) {
        if (inMemoryUserStorage.putUser(user) == null) {
            return null;
        }
        log.info("Получен PUT User");
        return inMemoryUserStorage.putUser(user);
    }

    public Collection<User> getUsers() {
        return inMemoryUserStorage.getMemoryUsers();
    }

    public User getUser(Integer id) {
        if (inMemoryUserStorage.getUserId(id) == null) {
            return null;
        }
        return inMemoryUserStorage.getUserId(id);
    }

    public Integer changeFriend(Integer id, Integer friendId) {
        if (getUser(id) == null) {
            return id;
        }
        if (getUser(friendId) == null) {
            return friendId;
        }
        inMemoryUserStorage.addFriendId(id, friendId);
        log.info("Получен друг PUT User");
        return null;
    }

    public Integer deleteFriend(Integer id, Integer friendId) {
        if (getUser(id) == null) {
            return id;
        }
        if (getUser(friendId) == null) {
            return friendId;
        }
        inMemoryUserStorage.deleteFriendId(id, friendId);
        return null;
    }

    public List<User> getUserFriend(Integer id) {
        return inMemoryUserStorage.getUserIdFriend(id);
    }

    public List<User> getCommonFriend(Integer id, Integer otherId) {
        return inMemoryUserStorage.getUsersCommonFriends(id, otherId);
    }
}
