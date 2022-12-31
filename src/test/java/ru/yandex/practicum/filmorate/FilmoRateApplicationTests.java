package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.UserDbStorage;

import java.time.LocalDate;
import java.util.*;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@ActiveProfiles("test")
@Sql({"/schema.sql", "/data-test.sql"})
class FilmoRateApplicationTests {

    public final UserDbStorage userStorage;
    private final FilmDbStorage filmDbStorage;
    private final JdbcTemplate jdbcTemplate;

    @Test
    public void testGetMemoryUsers() {
        Collection<User> users = userStorage.getMemoryUsers();
        User user = new User(2, "mail2@mail.ru", "dolore2", "Nick Name2", LocalDate.parse("1946-08-22"), new HashSet<>());
        Assertions.assertEquals(users.toArray()[1], user);
        Assertions.assertEquals(users.size(), 3);
    }

    @Test
    public void testGetUserId() {
        User user = userStorage.getUserId(3);
        User user2 = new User(3, "mail3@mail.ru", "dolore3", "Nick Name3", LocalDate.parse("1946-08-23"), new HashSet<>());
        Assertions.assertEquals(user2, user);
    }

    @Test
    public void TestAddFriendId() {
        userStorage.addFriendId(1, 2);
        Assertions.assertEquals(false, jdbcTemplate.queryForObject("SELECT friendship FROM friends WHERE user_id=1 AND friend_id=2", Boolean.class));
        userStorage.addFriendId(2, 1);
        Assertions.assertEquals(true, jdbcTemplate.queryForObject("SELECT friendship FROM friends WHERE user_id=1 AND friend_id=2", Boolean.class));
    }

    @Test
    public void TestDeleteFriendId() {
        userStorage.addFriendId(1, 2);
        userStorage.addFriendId(2, 1);
        userStorage.deleteFriendId(1, 2);
        Assertions.assertEquals(false, jdbcTemplate.queryForObject("SELECT friendship FROM friends WHERE user_id=2 AND friend_id=1", Boolean.class));
    }

    @Test
    public void TestGetUserIdFriend() {
        userStorage.addFriendId(2, 1);
        userStorage.addFriendId(2, 3);
        List<User> friends = userStorage.getUserIdFriend(2);
        Assertions.assertEquals(friends.size(), 2);
    }

    @Test
    public void TestGetUsersCommonFriends() {
        userStorage.addFriendId(2, 1);
        userStorage.addFriendId(2, 3);
        userStorage.addFriendId(1, 2);
        userStorage.addFriendId(1, 3);
        userStorage.addFriendId(3, 1);
        userStorage.addFriendId(3, 2);
        List<User> friends1 = userStorage.getUsersCommonFriends(1, 2);
        List<User> friends2 = userStorage.getUsersCommonFriends(2, 3);
        Assertions.assertEquals(friends1.size(), 1);
        Assertions.assertEquals(friends2.size(), 1);
    }

    @Test
    public void testGetMemoryFilms() {
        Collection<Film> films = filmDbStorage.getMemoryFilms();
        Film film = new Film(2, "nisi eiusmod2", "adipisicing2", LocalDate.parse("1967-03-22"), 102, new HashSet<>(), 2, new Rating(2, "PG"), new ArrayList<>());
        Assertions.assertEquals(films.toArray()[1], film);
        Assertions.assertEquals(films.size(), 3);
    }


    @Test
    public void testGetFilmId() {
        Film film = filmDbStorage.getFilmId(3);
        Film film2 = new Film(3, "nisi eiusmod3", "adipisicing3", LocalDate.parse("1967-03-23"), 103, new HashSet<>(), 3, new Rating(3, "PG-13"), new ArrayList<>());
        Assertions.assertEquals(film, film2);
    }

    @Test
    public void testAddLikeUserd() {
        filmDbStorage.addLikeUser(1, 2);
        filmDbStorage.addLikeUser(1, 3);
        filmDbStorage.addLikeUser(2, 3);
        Assertions.assertEquals(filmDbStorage.getFilmId(1).getRate(), 2);
        Assertions.assertEquals(filmDbStorage.getFilmId(2).getRate(), 3);
    }

    @Test
    public void testDeleteLikeUser() {
        filmDbStorage.addLikeUser(1, 1);
        filmDbStorage.addLikeUser(1, 2);
        filmDbStorage.addLikeUser(1, 3);
        filmDbStorage.deleteLikeUser(1, 3);
        Assertions.assertEquals(filmDbStorage.getFilmId(1).getRate(), 2);
    }

    @Test
    public void testCountPopularFilm() {
        List<Film> films = filmDbStorage.countPopularFilm(2);
        Assertions.assertEquals(films.get(0).getName(), "nisi eiusmod3");
        Assertions.assertEquals(films.get(1).getName(), "nisi eiusmod2");
    }
}
