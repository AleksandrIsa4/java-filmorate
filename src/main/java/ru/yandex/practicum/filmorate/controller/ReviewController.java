package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.BadRequestException;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.FeedService;
import ru.yandex.practicum.filmorate.service.ReviewService;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

@Validated
@RestController
@RequestMapping(
        value = "/reviews",
        consumes = MediaType.ALL_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
)
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;
    private final FeedService feedService;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public Review postReview(@RequestBody @Valid @NotNull Review review) {
        if (review.getUserId() == 0 || review.getFilmId() == 0) {
            throw new BadRequestException("Bad request. Review couldn't be null.");
        }
        Review currentReview = reviewService.postReview(review);
        if (currentReview == null) {
            throw new NotFoundException(HttpStatus.NOT_FOUND, "review id not found");
        }
        feedService.createReviewAddition(currentReview.getUserId(), currentReview.getReviewId());
        return currentReview;
    }

    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public Review reviewUpdate(@RequestBody @Valid @NotNull Review review) {
        Review currentReview = reviewService.putReview(review);
        feedService.createReviewUpdate(currentReview.getUserId(), currentReview.getReviewId());
        return currentReview;
    }

    @GetMapping(value = "/{reviewId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Review getReviewById(@PathVariable("reviewId") @NotNull Integer reviewId) {
        return reviewService.getReviewById(reviewId);
    }


    @DeleteMapping(value = "/{reviewId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public void deleteReview(@PathVariable("reviewId") @NotNull Integer reviewId) {
        Review currentReview = reviewService.getReviewById(reviewId);
        reviewService.deleteReview(reviewId);
        feedService.createReviewDeletion(currentReview.getUserId(), currentReview.getReviewId());
    }

    @GetMapping
    public Collection<Review> getReviewsWithCount(@RequestParam(value = "filmId", defaultValue = "0", required = false)
                                                  String filmId,
                                                  @RequestParam(value = "count", defaultValue = "10", required = false)
                                                  String count) {
        return reviewService.getReviewsWithCount(Integer.parseInt(filmId), Integer.parseInt(count));
    }

    @PutMapping(value = "/{id}/like/{userId}")
    public void addLike(@PathVariable("id") @NotNull Integer id,
                        @PathVariable("userId") @NotNull Integer userId) {
        reviewService.addLike(id, userId);
    }

    @PutMapping(value = "/{id}/dislike/{userId}")
    public void addDislike(@PathVariable("id") @NotNull Integer id,
                           @PathVariable("userId") @NotNull Integer userId) {
        reviewService.addDislike(id, userId);
    }

    @DeleteMapping(value = "/{id}/like/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public void deleteLike(@PathVariable("id") @NotNull Integer id,
                           @PathVariable("userId") @NotNull Integer userId) {
        reviewService.deleteLike(id, userId);
    }

    @DeleteMapping(value = "/{id}/dislike/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public void deleteDislike(@PathVariable("id") @NotNull Integer id,
                              @PathVariable("userId") @NotNull Integer userId) {
        reviewService.deleteDislike(id, userId);
    }
}
