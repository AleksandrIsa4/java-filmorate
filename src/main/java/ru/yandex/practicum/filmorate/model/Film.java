package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.validator.FilmValid;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Film {
    private int id;
    @NotBlank(message = "Film не может быть пустым")
    private String name;
    @Size(max = 200, message = "Film не может быть длиннее 200 символов")
    private String description;
    @FilmValid
    private LocalDate releaseDate;
    @Positive(message = "Film не может быть отрицательным")
    private long duration;
    private Set<Integer> like = new HashSet<>();
    private int rate;
}
