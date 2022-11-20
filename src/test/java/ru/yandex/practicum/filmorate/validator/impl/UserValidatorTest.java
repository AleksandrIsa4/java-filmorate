package ru.yandex.practicum.filmorate.validator.impl;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static ru.yandex.practicum.filmorate.validator.impl.utils.ValidatorTestUtils.getUserRequest;
import static ru.yandex.practicum.filmorate.validator.utils.ValidatorTestUtils.dtoHasErrorMessage;

public class UserValidatorTest {

    @Test
    void createUserFailLogin() {
        User user1 = getUserRequest(null, "mail@mail.ru", "Nick Name", LocalDate.parse("1946-08-20"));
        User user2 = getUserRequest("dol ore", "mail@mail.ru", "Nick Name", LocalDate.parse("1946-08-20"));
        Assertions.assertAll(
                () -> Assertions.assertTrue(dtoHasErrorMessage(user1, " User не может быть пустым")),
                () -> Assertions.assertTrue(dtoHasErrorMessage(user2, "login не должен содержать пробелы"))
        );
    }

    @Test
    void createUserFailEmail() {
        User user1 = getUserRequest("dolore", null, "Nick Name", LocalDate.parse("1946-08-20"));
        User user2 = getUserRequest("dolore", "mailmail.ru", "Nick Name", LocalDate.parse("1946-08-20"));
        Assertions.assertAll(
                () -> Assertions.assertTrue(dtoHasErrorMessage(user1, " User не может быть пустым")),
                () -> Assertions.assertTrue(dtoHasErrorMessage(user2, " User должен быть в виде email"))
        );
    }

    @Test
    void createUserFailBirthday() {
        User user1 = getUserRequest("dolore", "mail@mail.ru", "Nick Name", LocalDate.parse("2246-08-20"));
        Assertions.assertTrue(dtoHasErrorMessage(user1, " User не может быть в будущем"));
    }
}
