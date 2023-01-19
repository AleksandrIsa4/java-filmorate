package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import ru.yandex.practicum.filmorate.model.Review;
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

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public Review postReview(@RequestBody @Valid @NotNull Review review) {
        if (review.getUserId() < 0 && review.getFilmId() < 0) {
            throw new HttpClientErrorException(HttpStatus.NOT_FOUND);
        }
        return reviewService.postReview(review);
    }

    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> reviewUpdate(@RequestBody @Valid @NotNull Review review) {
        Review currentReview = reviewService.putReview(review);
        if (currentReview == null) {
            Map<String, Object> body = new LinkedHashMap<>();
            body.put("Запись не найдена с id ", review.getReviewId());
            body.put("Код ошибки", HttpStatus.NOT_FOUND.value());
            return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(currentReview, HttpStatus.OK);
    }

    @GetMapping(value = "/{reviewId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getReviewById(@PathVariable("reviewId") @NotNull Integer reviewId) {
        Review currentReview = reviewService.getReviewById(reviewId);
        if (currentReview == null) {
            Map<String, Object> body = new LinkedHashMap<>();
            body.put("Запись не найдена с id ", reviewId);
            body.put("Код ошибки", HttpStatus.NOT_FOUND.value());
            return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(currentReview, HttpStatus.OK);

    }


    @DeleteMapping(value = "/{reviewId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public void deleteReview(@PathVariable("reviewId") @NotNull Integer reviewId) {
        reviewService.deleteReview(reviewId);
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
