package ru.yandex.practicum.filmorate.storage;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;

@Component
public class UserDbStorage implements UserStorage {

    private int generator = 0;
    private final JdbcTemplate jdbcTemplate;

    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        maxId();
    }

    @Override
    public void deleteUser(Integer id) {
        jdbcTemplate.update("DELETE FROM user_kino WHERE user_id=?", id);
    }

    @Override
    public User postUser(User user) {
        additionUser(user);
        jdbcTemplate.update("INSERT INTO user_kino VALUES (?,?,?,?,?)", user.getId(), user.getEmail(), user.getLogin(), user.getName(), user.getBirthday());
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("SELECT * FROM user_kino WHERE user_id= ?", user.getId());
        userRows.next();
        return getUserBD(userRows);
    }

    @Override
    public User putUser(User user) {
        jdbcTemplate.update("UPDATE user_kino SET email=?, login=?, name=?, birthday=? WHERE user_id=?", user.getEmail(), user.getLogin(), user.getName(), user.getBirthday(), user.getId());
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("SELECT * FROM user_kino WHERE user_id= ?", user.getId());
        if (userRows.next()) {
            return getUserBD(userRows);
        } else {
            return null;
        }
    }

    @Override
    public Collection<User> getMemoryUsers() {
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("SELECT * FROM user_kino");
        Collection<User> usersSQL = new ArrayList<>();
        while (userRows.next()) {
            usersSQL.add(getUserBD(userRows));
        }
        return usersSQL;
    }

    @Override
    public User getUserId(Integer id) {
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("SELECT * FROM user_kino WHERE user_id= ?", id);
        userRows.next();
        if (userRows.last()) {
            return getUserBD(userRows);
        } else {
            return null;
        }
    }

    @Override
    public void addFriendId(Integer id, Integer friendId) {
        SqlRowSet userRowsFriendId = jdbcTemplate.queryForRowSet("SELECT user_id FROM friends WHERE user_id=? AND friend_id=?", friendId, id);
        SqlRowSet userRowsIdFriend = jdbcTemplate.queryForRowSet("SELECT user_id FROM friends WHERE user_id=? AND friend_id=?", id, friendId);
        if (userRowsFriendId.next()) {
            if (userRowsIdFriend.next()) {
                jdbcTemplate.update("UPDATE friends SET friendship=true WHERE user_id=? AND friend_id=?", id, friendId);
                jdbcTemplate.update("UPDATE friends SET friendship=true WHERE user_id=? AND friend_id=?", friendId, id);
            } else {
                jdbcTemplate.update("INSERT INTO friends(user_id,friend_id,friendship) VALUES (?,?,true)", id, friendId);
                jdbcTemplate.update("UPDATE friends SET friendship=true WHERE user_id=? AND friend_id=?", friendId, id);
            }
        } else if (userRowsIdFriend.next()) {
            jdbcTemplate.update("UPDATE friends SET friendship=false WHERE user_id=? AND friend_id=?", id, friendId);
        } else {
            jdbcTemplate.update("INSERT INTO friends(user_id,friend_id,friendship) VALUES (?,?,false)", id, friendId);
        }
    }

    @Override
    public void deleteFriendId(Integer id, Integer friendId) {
        SqlRowSet userRowsFriendId = jdbcTemplate.queryForRowSet("SELECT user_id FROM friends WHERE user_id=? AND friend_id=?", friendId, id);
        SqlRowSet userRowsIdFriend = jdbcTemplate.queryForRowSet("SELECT user_id FROM friends WHERE user_id=? AND friend_id=?", id, friendId);
        if (userRowsFriendId.next()) {
            if (userRowsIdFriend.next()) {
                jdbcTemplate.update("DELETE FROM friends WHERE user_id=? AND friend_id=?", id, friendId);
                jdbcTemplate.update("UPDATE friends SET friendship=false WHERE user_id=? AND friend_id=?", friendId, id);
            } else {
                jdbcTemplate.update("UPDATE friends SET friendship=false WHERE user_id=? AND friend_id=?", friendId, id);
            }
        } else if (userRowsIdFriend.next()) {
            jdbcTemplate.update("DELETE FROM friends WHERE user_id=? AND friend_id=?", id, friendId);
        }
    }

    @Override
    public List<User> getUserIdFriend(Integer id) {
        List<User> friends = new ArrayList<>();
        SqlRowSet userRowsFriends = jdbcTemplate.queryForRowSet("SELECT friend_id FROM friends WHERE user_id=?", id);
        while (userRowsFriends.next()) {
            friends.add(getUserId(userRowsFriends.getInt("friend_id")));
        }
        return friends;
    }

    @Override
    public List<User> getUsersCommonFriends(Integer id, Integer otherId) {
        List<User> friends = new ArrayList<>();
        SqlRowSet userRowsFriends = jdbcTemplate.queryForRowSet("SELECT friend_id FROM friends WHERE user_id=? OR user_id=? GROUP BY friend_id HAVING COUNT(*) > 1", id, otherId);
        while (userRowsFriends.next()) {
            friends.add(getUserId(userRowsFriends.getInt("friend_id")));
        }
        return friends;
    }

    private void maxId() {
        Integer i = jdbcTemplate.queryForObject("SELECT MAX(user_id) FROM user_kino", Integer.class);
        if (i == null) {
            generator = 0;
        } else {
            generator = i;
        }
    }

    private void additionUser(User user) {
        if (user.getName() == null || user.getName().equals("")) {
            user.setName(user.getLogin());
        }
        if (user.getId() == 0) {
            user.setId(++generator);
        }
    }

    private User getUserBD(SqlRowSet userRows) {
        User userSql = new User();
        userSql.setId(userRows.getInt("user_id"));
        userSql.setEmail(userRows.getString("email"));
        userSql.setLogin(userRows.getString("login"));
        userSql.setName(userRows.getString("name"));
        userSql.setBirthday(userRows.getDate("birthday").toLocalDate());
        SqlRowSet userRowsFriends = jdbcTemplate.queryForRowSet("SELECT friend_id FROM friends WHERE user_id=?", userRows.getInt("user_id"));
        Set<Integer> friends = new HashSet<>();
        while (userRowsFriends.next()) {
            friends.add(userRowsFriends.getInt("friend_id"));
        }
        userSql.setFriends(friends);
        return userSql;
    }
}
