package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Review {
    private int reviewId;
    @NotBlank(message = "Отзыв не может быть пустым")
    private String content;
    @NotNull
    private Boolean isPositive;
    @Positive @NotNull
    private int userId;
    @Positive @NotNull
    private int filmId;
    private int useful;
}
