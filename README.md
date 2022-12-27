# java-filmorate
Template repository for Filmorate project.
![Диаграмма БД](https://user-images.githubusercontent.com/108830655/209578935-745b8f5d-7e66-4d33-8ca1-302227dc4dd0.png)

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


Несколько комментариев с моей стороны.

1. Например таблица like_users может иметь составной ключ из user_id и film_id и, по сути, поле id не нужно.
Та же логика может быть применена и к таблицам genre_film и friends.
2. В таблице film поле release_date хранит Localdate. Мне кажется лучше будет указать timestamp, потому что этот тип предназначен для работы с датой и временем, как писали в нашей лекции про типы данных в PostgreSQL.
Больше добавить ничего не смогу.
