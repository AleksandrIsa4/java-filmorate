package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;

@Component
public class InMemoryUserStorage implements UserStorage {

    private int generator = 0;
    private final Map<Integer, User> users = new HashMap<>();

    @Override
    public void deleteUser(Integer id) {
        users.remove(id);
    }

    @Override
    public User postUser(User user) {
        additionUser(user);
        users.put(user.getId(), user);
        return users.get(user.getId());
    }

    @Override
    public User putUser(User user) {
        if (users.containsKey(user.getId())) {
            users.put(user.getId(), user);
            return users.get(user.getId());
        }
        return null;
    }

    @Override
    public Collection<User> getMemoryUsers() {
        return users.values();
    }

    @Override
    public User getUserId(Integer id) {
        if (users.containsKey(id)) {
            return users.get(id);
        }
        return null;
    }

    @Override
    public void addFriendId(Integer id, Integer friendId) {
        Set<Integer> friends = users.get(id).getFriends();
        friends.add(friendId);
        users.get(id).setFriends(friends);
        friends = users.get(friendId).getFriends();
        friends.add(id);
        users.get(friendId).setFriends(friends);
    }

    @Override
    public void deleteFriendId(Integer id, Integer friendId) {
        Set<Integer> friends = users.get(id).getFriends();
        friends.remove(friendId);
        users.get(id).setFriends(friends);
        friends = users.get(friendId).getFriends();
        friends.remove(id);
        users.get(friendId).setFriends(friends);
    }

    @Override
    public List<User> getUserIdFriend(Integer id) {
        List<User> friends = new ArrayList<>();
        if (users.containsKey(id)) {
            for (Integer i : users.get(id).getFriends()) {
                friends.add(users.get(i));
            }
            return friends;
        }
        return null;
    }

    @Override
    public List<User> getUsersCommonFriends(Integer id, Integer otherId) {
        Set<Integer> friendsId = new HashSet<>(users.get(id).getFriends());
        friendsId.retainAll(users.get(otherId).getFriends());
        List<User> friends = new ArrayList<>();
        for (Integer i : friendsId) {
            friends.add(users.get(i));
        }
        return friends;
    }

    private void additionUser(User user) {
        if (user.getName() == null || user.getName().equals("")) {
            user.setName(user.getLogin());
        }
        if (user.getId() == 0) {
            user.setId(++generator);
        }
    }
}
