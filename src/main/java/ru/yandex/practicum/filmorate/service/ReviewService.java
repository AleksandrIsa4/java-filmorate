package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.ReviewsDBStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewService {

    @Autowired
    private final ReviewsDBStorage reviewsDBStorage;

    public Review postReview(Review review) {
        return reviewsDBStorage.postReview(review);
    }

    public Review putReview(Review review) {
        return reviewsDBStorage.putReview(review);
    }

    public Review getReviewById(Integer id) {
        return reviewsDBStorage.getReviewById(id);
    }

    public void deleteReview(Integer reviewId) {
        reviewsDBStorage.deleteReview(reviewId);
    }

    public Collection<Review> getReviewsWithCount(Integer filmId, Integer count) {
        return reviewsDBStorage.getReviewsWithCount(filmId, count);
    }

    public void addLike(Integer id, Integer userId){
        reviewsDBStorage.addLike(id, userId);
    }

    public void addDislike(Integer id, Integer userId){
        reviewsDBStorage.addDislike(id, userId);
    }

    public void deleteLike(Integer id, Integer userId) {
        reviewsDBStorage.deleteLike(id, userId);
    }

    public void deleteDislike(Integer id, Integer userId) {
        reviewsDBStorage.deletDislLike(id, userId);
    }

    }
