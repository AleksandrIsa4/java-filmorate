package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.validation.constraints.*;
import java.time.LocalDate;

@Data
public class User {
    private int id;
    @NotBlank(message = " User не может быть пустым")
    @Email(message = " User должен быть в виде email")
    private String email;
    @NotBlank(message = " User не может быть пустым")
    @Pattern(regexp = "[^ ]*", message = "login не должен содержать пробелы")
    private String login;
    private String name;
    @Past(message = " User не может быть в будущем")
    private LocalDate birthday;
}
