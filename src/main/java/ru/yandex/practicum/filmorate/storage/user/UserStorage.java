package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.List;

public interface UserStorage {

    public void deleteUser(Integer id);

    public User postUser(User user);

    public User putUser(User user);

    public Collection<User> getMemoryUsers();

    public User getUserId(Integer id);

    public void addFriendId(Integer id, Integer friendId);

    public void deleteFriendId(Integer id, Integer friendId);

    public List<User> getUserIdFriend(Integer id);

    public List<User> getUsersCommonFriends(Integer id, Integer otherId);
}
