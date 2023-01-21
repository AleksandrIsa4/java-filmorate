package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.storage.FeedDbStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FeedService {
    private final static int LIKE_ID = 1;
    private final static int REVIEW_ID = 2;
    private final static int FRIEND_ID = 3;
    private final static int ADD_ID = 1;
    private final static int UPDATE_ID = 2;
    private final static int DELETE_ID = 3;
    private final FeedDbStorage storage;

    public List<Event> getFeed(Integer id) {
        return storage.getFeed(id);
    }

    public void createLikeAddition(int userId, int filmId) {
        storage.createEvent(userId, filmId, LIKE_ID, ADD_ID);
    }

    public void createReviewAddition(int userId, int reviewId) {
        storage.createEvent(userId, reviewId, REVIEW_ID, ADD_ID);
    }

    public void createFriendAddition(int userId, int friendId) {
        storage.createEvent(userId, friendId, FRIEND_ID, ADD_ID);
    }

    public void createReviewUpdate(int userId, int reviewId) {
        storage.createEvent(userId, reviewId, REVIEW_ID, UPDATE_ID);
    }

    public void createLikeDeletion(int userId, int filmId) {
        storage.createEvent(userId, filmId, LIKE_ID, DELETE_ID);
    }

    public void createReviewDeletion(int userId, int reviewId) {
        storage.createEvent(userId, reviewId, REVIEW_ID, DELETE_ID);
    }

    public void createFriendDeletion(int userId, int friendId) {
        storage.createEvent(userId, friendId, FRIEND_ID, DELETE_ID);
    }
}
