package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private int id;
    @NotBlank(message = "User не может быть пустым")
    @Email(message = "User должен быть в виде email")
    private String email;
    @NotBlank(message = "User не может быть пустым")
    @Pattern(regexp = "[^ ]*", message = "User не должен содержать пробелы")
    private String login;
    private String name;
    @Past(message = "User не может быть в будущем")
    private LocalDate birthday;
    private Set<Integer> friends;
}
