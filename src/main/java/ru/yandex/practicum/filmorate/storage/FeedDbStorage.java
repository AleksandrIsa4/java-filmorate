package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class FeedDbStorage {
    private final int LIKE_ID = 1;
    private final int REVIEW_ID = 2;
    private final int FRIEND_ID = 3;
    private final int ADD_ID = 1;
    private final int UPDATE_ID = 2;
    private final int DELETE_ID = 3;

    private final JdbcTemplate jdbcTemplate;

    public List<Event> getFeed(Integer userId) {
        String sql = "SELECT f.timestamp, f.user_id, e.name event_type, o.name operation, " +
                "f.id event_id, f.film_id, f.review_id, f.friend_id " +
                "FROM feed f LEFT JOIN event e ON f.event_type = e.id " +
                "LEFT JOIN operation o ON f.operation_id = o.id " +
                "WHERE f.user_id IN (?)";
        //String inSql = String.join(",", Collections.nCopies(friends.size(), "?"));
        /*String sql = String.format("SELECT f.timestamp, f.user_id, e.name event_type, o.name operation, " +
                "f.id event_id, f.film_id, f.review_id, f.friend_id " +
                "FROM feed f LEFT JOIN event e ON f.event_type = e.id " +
                "LEFT JOIN operation o ON f.operation_id = o.id " +
                "WHERE f.user_id IN (%s)", inSql);*/
        //String sql = "SELECT * FROM feed ";
        return jdbcTemplate.query(sql, ((rs, rowNum) -> makeEvent(rs)), userId);
        //return jdbcTemplate.query(sql, ((rs, rowNum) -> makeEvent(rs)), friends.stream().map(User::getId).collect(Collectors.toList()).toArray());
    }

    public void createLikeAddition(int userId, int filmId) {
        createEvent(userId, filmId, LIKE_ID, ADD_ID);
    }

    public void createReviewAddition(int userId, int reviewId) {
        createEvent(userId, reviewId, REVIEW_ID, ADD_ID);
    }

    public void createFriendAddition(int userId, int friendId) {
        createEvent(userId, friendId, FRIEND_ID, ADD_ID);
    }

    public void createLikeUpdate(int userId, int filmId) {
        createEvent(userId, filmId, LIKE_ID, UPDATE_ID);
    }

    public void createReviewUpdate(int userId, int reviewId) {
        createEvent(userId, reviewId, REVIEW_ID, UPDATE_ID);
    }

    public void createFriendUpdate(int userId, int friendId) {
        createEvent(userId, friendId, FRIEND_ID, UPDATE_ID);
    }

    public void createLikeDeletion(int userId, int filmId) {
        createEvent(userId, filmId, LIKE_ID, DELETE_ID);
    }

    public void createReviewDeletion(int userId, int reviewId) {
        createEvent(userId, reviewId, REVIEW_ID, DELETE_ID);
    }

    public void createFriendDeletion(int userId, int friendId) {
        createEvent(userId, friendId, FRIEND_ID, DELETE_ID);
    }

    private void createEvent(int userId, int entityId, int eventId, int operationId) {
        String sql = "INSERT INTO feed (timestamp, user_id, event_type, operation_id, %s) VALUES (?, ?, ?, ?, ?)";
        //String sql = "INSERT INTO feed (user_id, event_type, operation_id, %s) VALUES (?, ?, ?, ?)";
        String sqlInsert;
        switch(eventId) {
            case LIKE_ID:
                sqlInsert = String.format(sql, "film_id");
                break;
            case REVIEW_ID:
                sqlInsert = String.format(sql, "review_id");
                break;
            case FRIEND_ID:
                sqlInsert = String.format(sql, "friend_id");
                break;
            default:
                sqlInsert = "";
                break;
        }
        jdbcTemplate.update(sqlInsert, Timestamp.from(Instant.now()), userId, eventId, operationId, entityId);
        //jdbcTemplate.update(sqlInsert, userId, eventId, operationId, entityId);
    }

    private Event makeEvent(ResultSet rs) throws SQLException {
        Event event = new Event();
        event.setTimestamp(rs.getTimestamp("timestamp").getTime());
        event.setUserId(rs.getInt("user_id"));
        event.setEventType(rs.getString("event_type"));
        event.setOperation(rs.getString("operation"));
        event.setEventId(rs.getInt("event_id"));
        /*Optional<Integer> filmId = Optional.ofNullable(rs.getInt("film_id"));
        Optional<Integer> reviewId = Optional.ofNullable(rs.getInt("review_id"));
        Optional<Integer> friendId = Optional.ofNullable(rs.getInt("friend_id"));*/
        event.setEntityId(rs.getInt("film_id"));
        event.setEntityId(rs.getInt("review_id"));
        event.setEntityId(rs.getInt("friend_id"));
        return event;
    }
}