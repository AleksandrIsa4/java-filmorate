# java-filmorate
Template repository for Filmorate project.
![Диаграмма БД](https://user-images.githubusercontent.com/108830655/209577613-c1e88940-a2c1-4218-ac4f-55f4489485fb.png)

Примеры запросов:

Добавление пользователя
jdbcTemplate.update("INSERT INTO user_kino VALUES (?,?,?,?,?)", user.getId(), user.getEmail(), user.getLogin(), user.getName(), user.getBirthday());
Изменение пользователя
jdbcTemplate.update("UPDATE user_kino SET email=?, login=?, name=?, birthday=? WHERE user_id=?", user.getEmail(), user.getLogin(), user.getName(), user.getBirthday(), user.getId());
Вывод всех пользователей
jdbcTemplate.queryForRowSet("SELECT * FROM user_kino");
Вывод конкретного пользователя
jdbcTemplate.queryForRowSet("SELECT * FROM user_kino WHERE user_id= ?", id);
Добавление друга
jdbcTemplate.update("UPDATE friends SET friendship=true WHERE user_id=? AND friend_id=?", id, friendId);
jdbcTemplate.update("UPDATE friends SET friendship=true WHERE user_id=? AND friend_id=?", friendId, id);
Удаление друга
jdbcTemplate.update("DELETE FROM friends WHERE user_id=? AND friend_id=?", id, friendId);
jdbcTemplate.update("UPDATE friends SET friendship=false WHERE user_id=? AND friend_id=?", friendId, id);
Показ друзей
jdbcTemplate.queryForRowSet("SELECT friend_id FROM friends WHERE user_id=?", id);
Показ общих друзей
jdbcTemplate.queryForRowSet("SELECT friend_id FROM friends WHERE user_id=? OR user_id=? GROUP BY friend_id HAVING COUNT(*) > 1", id, otherId);

Добавление фильма
jdbcTemplate.update("INSERT INTO film VALUES (?,?,?,?,?,?,?)", film.getId(), film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(), film.getRate(), film.getMpa().getId());
Изменение фильма
jdbcTemplate.update("UPDATE film SET name=?, description=?, release_date=?, duration=?, rate=?, rating_id=? WHERE film_id=?", film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(), film.getRate(), film.getMpa().getId(),film.getId());
Вывод всех фильмов
jdbcTemplate.queryForRowSet("SELECT * FROM film AS f INNER JOIN mpa AS m ON m.id = f.rating_id");
Вывод фильма
jdbcTemplate.queryForRowSet("SELECT * FROM film AS f INNER JOIN mpa AS m ON m.id = f.rating_id WHERE film_id= ?", id);
Добавить лайк от пользователя
jdbcTemplate.queryForRowSet("SELECT user_id FROM like_users WHERE user_id=? AND film_id=?", userId, id);
Удалить лайк от пользователя
jdbcTemplate.queryForRowSet("SELECT user_id FROM like_users WHERE user_id=? AND film_id=?", userId, id);
Вывод популярных фильмов
jdbcTemplate.queryForRowSet("SELECT film_id, rate FROM film GROUP BY film_id, rate ORDER BY rate DESC LIMIT ?",count);
