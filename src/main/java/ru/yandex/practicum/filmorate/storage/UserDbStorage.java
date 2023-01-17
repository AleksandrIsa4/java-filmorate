package ru.yandex.practicum.filmorate.storage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;

@Component
public class UserDbStorage implements UserStorage {

    private int generator = 0;
    private final JdbcTemplate jdbcTemplate;
    //@Autowired
    // @Qualifier("filmDbStorage")
    private final FilmStorage filmDbStorage;

    public UserDbStorage(JdbcTemplate jdbcTemplate, FilmStorage filmDbStorage) {
        this.jdbcTemplate = jdbcTemplate;
        maxId();
        this.filmDbStorage = filmDbStorage;
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

    public List<Film> getUserIdRecomment(Integer id) {
        //Получения списка фильмов с лайками
        SqlRowSet userLikeRows = jdbcTemplate.queryForRowSet("SELECT DISTINCT film_id FROM like_users WHERE user_id=?", id);
        Set<Integer> usersLike = new HashSet<>();
        while (userLikeRows.next()) {
            usersLike.add(userLikeRows.getInt("film_id"));
        }
        //Получения списка всех фильмов
        SqlRowSet filmAllRows = jdbcTemplate.queryForRowSet("SELECT film_id FROM film");
        List<Integer> filmsAll = new ArrayList<>();
        while (filmAllRows.next()) {
            filmsAll.add(filmAllRows.getInt("film_id"));
        }
        //Получение карты с данными ранжирования лайков (Map<userId, HashMap<filmId, оценка>>)
        Map<Integer, HashMap<Integer, Double>> data = new HashMap<>();
        Integer userId;
        Integer filmId;
        HashMap<Integer, Double> filmLike = new HashMap<Integer, Double>();
        SqlRowSet dataRows = jdbcTemplate.queryForRowSet("SELECT user_id, film_id FROM like_users");
        while (dataRows.next()) {
            userId = dataRows.getInt("user_id");
            filmId = dataRows.getInt("film_id");
            if (data.get(userId) != null) {
                filmLike = data.get(userId);
            }
            filmLike.put(filmId, 1.0);
            data.put(userId, filmLike);
        }
        //Получение матрицы различий и частот
        Map<Integer, Map<Integer, Double>> diff = new HashMap<>();
        Map<Integer, Map<Integer, Integer>> freq = new HashMap<>();
        for (HashMap<Integer, Double> user : data.values()) {
            for (Map.Entry<Integer, Double> e : user.entrySet()) {
                if (!diff.containsKey(e.getKey())) {
                    diff.put(e.getKey(), new HashMap<Integer, Double>());
                    freq.put(e.getKey(), new HashMap<Integer, Integer>());
                }
                for (Map.Entry<Integer, Double> e2 : user.entrySet()) {
                    int oldCount = 0;
                    if (freq.get(e.getKey()).containsKey(e2.getKey())) {
                        oldCount = freq.get(e.getKey()).get(e2.getKey()).intValue();
                    }
                    double oldDiff = 0.0;
                    if (diff.get(e.getKey()).containsKey(e2.getKey())) {
                        oldDiff = diff.get(e.getKey()).get(e2.getKey()).doubleValue();
                    }
                    double observedDiff = e.getValue() - e2.getValue();
                    freq.get(e.getKey()).put(e2.getKey(), oldCount + 1);
                    diff.get(e.getKey()).put(e2.getKey(), oldDiff + observedDiff);
                }
            }
        }
        for (Integer j : diff.keySet()) {
            for (Integer i : diff.get(j).keySet()) {
                double oldValue = diff.get(j).get(i).doubleValue();
                int count = freq.get(j).get(i).intValue();
                diff.get(j).put(i, oldValue / count);
            }
        }
        //Получение матрицы предсказания (Map<userId, HashMap<filmId, оценка>>)
        Map<Integer, HashMap<Integer, Double>> outputData = new HashMap<>();
        HashMap<Integer, Double> uPred = new HashMap<Integer, Double>();
        HashMap<Integer, Integer> uFreq = new HashMap<Integer, Integer>();
        for (Integer j : diff.keySet()) {
            uFreq.put(j, 0);
            uPred.put(j, 0.0);
        }
        for (Map.Entry<Integer, HashMap<Integer, Double>> e : data.entrySet()) {
            for (Integer jFilm : e.getValue().keySet()) {
                for (Integer kFilm : diff.keySet()) {
                    try {
                        double predictedValue = diff.get(kFilm).get(jFilm).doubleValue() + e.getValue().get(jFilm).doubleValue();
                        double finalValue = predictedValue * freq.get(kFilm).get(jFilm).intValue();
                        uPred.put(kFilm, uPred.get(kFilm) + finalValue);
                        uFreq.put(kFilm, uFreq.get(kFilm) + freq.get(kFilm).get(jFilm).intValue());
                    } catch (NullPointerException e1) {
                    }
                }
            }
            HashMap<Integer, Double> clean = new HashMap<Integer, Double>();
            for (Integer jFilm : uPred.keySet()) {
                if (uFreq.get(jFilm) > 0) {
                    clean.put(jFilm, uPred.get(jFilm).doubleValue() / uFreq.get(jFilm).intValue());
                }
            }
            for (Integer jFilm : filmsAll) {
                if (e.getValue().containsKey(jFilm)) {
                    clean.put(jFilm, e.getValue().get(jFilm));
                } else if (!clean.containsKey(jFilm)) {
                    clean.put(jFilm, -1.0);
                }
            }
            outputData.put(e.getKey(), clean);
        }
        //Получение списка фильмов рекомендованных для пользователя с id за исключением уже с лайком от пользователя
        List<Film> filmIdRecommend = new ArrayList<>();
        try {
            for (Map.Entry<Integer, Double> like : outputData.get(id).entrySet()) {
                if (like.getValue() > 0.5 && !(usersLike.contains(like.getKey()))) {
                    filmIdRecommend.add(filmDbStorage.getFilmId(like.getKey()));
                }
            }
        } catch (NullPointerException e) {
        }
        return filmIdRecommend;
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
