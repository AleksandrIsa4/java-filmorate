package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Event;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

@Component
@RequiredArgsConstructor
public class FeedDbStorage {
    private final static int LIKE_ID = 1;
    private final static int REVIEW_ID = 2;
    private final static int FRIEND_ID = 3;
    private final JdbcTemplate jdbcTemplate;

    public List<Event> getFeed(Integer userId) {
        boolean isExist = jdbcTemplate.queryForObject("SELECT EXISTS(SELECT * FROM user_kino WHERE user_id = ?)",
                ((rs, rowNum) -> rs.getBoolean(1)), userId);
        if (!isExist) return null;
        String sql = "SELECT f.timestamp, f.user_id, e.name event_type, o.name operation, " +
                "f.id event_id, f.film_id, f.review_id, f.friend_id " +
                "FROM feed f LEFT JOIN event e ON f.event_type = e.id " +
                "LEFT JOIN operation o ON f.operation_id = o.id " +
                "WHERE f.user_id IN (?)";
        return jdbcTemplate.query(sql, ((rs, rowNum) -> makeEvent(rs)), userId);
    }

    public void createEvent(int userId, int entityId, int eventId, int operationId) {
        String sql = "INSERT INTO feed (timestamp, user_id, event_type, operation_id, %s) VALUES (?, ?, ?, ?, ?)";
        String sqlInsert;
        switch (eventId) {
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
    }

    private Event makeEvent(ResultSet rs) throws SQLException {
        Event event = new Event();
        event.setTimestamp(rs.getTimestamp("timestamp").getTime());
        event.setUserId(rs.getInt("user_id"));
        event.setEventType(rs.getString("event_type"));
        event.setOperation(rs.getString("operation"));
        event.setEventId(rs.getInt("event_id"));
        int filmId = rs.getInt("film_id");
        int reviewId = rs.getInt("review_id");
        int friendId = rs.getInt("friend_id");
        if (filmId != 0) {
            event.setEntityId(filmId);
        } else if (reviewId != 0) {
            event.setEntityId(reviewId);
        } else if (friendId != 0) {
            event.setEntityId(friendId);
        }
        return event;
    }
}