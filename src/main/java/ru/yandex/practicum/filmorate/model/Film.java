package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import ru.yandex.practicum.filmorate.validator.FilmValid;

import javax.validation.constraints.*;
import java.time.LocalDate;

@Data
@FilmValid
public class Film {
    private int id;
    @NotBlank(message = " Film не может быть пустым")
    private String name;
    @Size(max = 200, message = " Film не может быть длиннее 200 символов")
    private String description;
   // @NotNull(message = " Film не может быть пустым")
    private LocalDate releaseDate;
    @Positive(message = " Film не может быть отрицательным")
    private long duration;
}
