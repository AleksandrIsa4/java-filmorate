package ru.yandex.practicum.filmorate.storage;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class FilmDbStorage implements FilmStorage {

    private int generator = 0;

    private final JdbcTemplate jdbcTemplate;
    private final DirectorDbStorage directorDbStorage;

    public FilmDbStorage(JdbcTemplate jdbcTemplate, DirectorDbStorage directorDbStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.directorDbStorage = directorDbStorage;
        maxId();
    }

    @Override
    public void deleteFilm(Integer id) {
        jdbcTemplate.update("DELETE FROM film WHERE film_id=?", id);
    }

    @Override
    public Film postFilm(Film film) {
        additionFilm(film);
        jdbcTemplate.update("INSERT INTO film VALUES (?,?,?,?,?,?,?)",
                film.getId(),
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                0,
                film.getMpa().getId());
        if (film.getGenres() != null) {
            Set<Genre> genreSet = Set.copyOf(film.getGenres());
            for (Genre genre : genreSet) {
                jdbcTemplate.update("INSERT INTO genre_film(film_id,genre_id) VALUES (?,?)",
                        film.getId(),
                        genre.getId());
            }
        }
        if (film.getDirectors() != null) {
            for (Director director : film.getDirectors()) {
                directorDbStorage.addToFilm(film.getId(), director.getId());
            }
        }
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet("SELECT * FROM film AS f " +
                "INNER JOIN mpa AS m ON m.id = f.rating_id " +
                "WHERE film_id= ?", film.getId());
        filmRows.next();
        return getFilmBD(filmRows);
    }

    @Override
    public Film putFilm(Film film) {
        jdbcTemplate.update("UPDATE film SET name=?, description=?, release_date=?, duration=?, rating_id=? WHERE film_id=?", film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(), film.getMpa().getId(), film.getId());
        if (film.getGenres() != null) {
            // удаление всех жанров фильма перед новой записью
            jdbcTemplate.update("DELETE FROM genre_film WHERE film_id=?", film.getId());
            Set<Genre> genreSet = Set.copyOf(film.getGenres());
            for (Genre genre : genreSet) {
                jdbcTemplate.update("INSERT INTO genre_film(film_id,genre_id) VALUES (?,?)", film.getId(), genre.getId());
            }
        }
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet("SELECT * FROM film AS f INNER JOIN mpa AS m ON m.id = f.rating_id WHERE film_id= ?", film.getId());
        if (filmRows.next()) {
            return getFilmBD(filmRows);
        } else {
            return null;
        }
    }

    @Override
    public Collection<Film> getMemoryFilms() {
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet("SELECT * FROM film AS f INNER JOIN mpa AS m ON m.id = f.rating_id");
        Collection<Film> filmsSQL = new ArrayList<>();
        while (filmRows.next()) {
            filmsSQL.add(getFilmBD(filmRows));
        }
        return filmsSQL;
    }

    @Override
    public Collection<Film> searchFilmByQuery(String query, String[] by) {
        boolean queryFind = false;
        boolean byTitle = false;
        boolean byDirector = false;
        Collection<Film> allFilm = getMemoryFilms();
        Collection<Film> result = new HashSet<>();
        Map<Integer, List<Director>> directorsToFilms = directorDbStorage.findAllToFilm();
        for (Film film : allFilm) {
            String directorName = "";
            if (directorsToFilms.containsKey(film.getId())) {
                film.setDirectors(directorsToFilms.get(film.getId()));
            }
            for (Director director : directorDbStorage.findAllToFilm(film.getId())) {
                directorName = director.getName().toLowerCase();
            }
            if (by != null) {
                for (String target : by) {
                    if (target.equalsIgnoreCase("title")) {
                        byTitle = film.getName().toLowerCase().contains(query);
                        if (directorName.isEmpty()) film.setDirectors(new ArrayList<>());
                        if (byTitle) { result.add(film); }
                    }
                    if (target.equalsIgnoreCase("director")) {
                        byDirector = directorName.contains(query);
                        if (byDirector) { result.add(film); }
                    }
                }
            } else {
                String scope = film.getName().toLowerCase() + film.getDescription().toLowerCase() + directorName;
                queryFind = scope.contains(query);
                if (queryFind) { result.add(film); }
            }
        }
        return result.stream().sorted(Comparator.comparingInt(Film::getRate).reversed())
                .collect(Collectors.toList());
    }

    @Override
    public Collection<Film> searchCommonFilm(String userId, String friendId) {
        Collection<Film> filmsSQL = new ArrayList<>();
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet("SELECT * FROM film AS f "
                + "INNER JOIN mpa AS m ON m.id = f.rating_id "
                + "INNER JOIN like_users AS lu ON lu.film_id = f.film_id "
                + "INNER JOIN friends AS fr ON fr.user_id = lu.user_id "
                + "WHERE fr.user_id = ? AND fr.friend_id = ? "
                + "ORDER BY rate DESC", userId, friendId);
        while (filmRows.next()) {
            filmsSQL.add(getFilmBD(filmRows));
        }
        return filmsSQL;
    }

    @Override
    public Film getFilmId(Integer id) {
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet("SELECT * FROM film AS f INNER JOIN mpa AS m ON m.id = f.rating_id WHERE film_id= ?", id);
        filmRows.next();
        if (filmRows.last()) {
            return getFilmBD(filmRows);
        } else {
            return null;
        }
    }

    @Override
    public void addLikeUser(Integer id, Integer userId) {
        SqlRowSet userRowsLike = jdbcTemplate.queryForRowSet("SELECT user_id FROM like_users WHERE user_id=? AND film_id=?", userId, id);
        userRowsLike.next();
        //Проверка, что запись не существует
        if (!userRowsLike.last()) {
            jdbcTemplate.update("INSERT INTO like_users(user_id,film_id) VALUES (?,?)", userId, id);
            jdbcTemplate.update("UPDATE film SET rate=rate+1 WHERE film_id=?", id);
        }
    }

    @Override
    public void deleteLikeUser(Integer id, Integer userId) {
        SqlRowSet userRowsLike = jdbcTemplate.queryForRowSet("SELECT user_id FROM like_users WHERE user_id=? AND film_id=?", userId, id);
        userRowsLike.next();
        //Проверка, что запись уже существует
        if (userRowsLike.last()) {
            jdbcTemplate.update("DELETE FROM like_users WHERE user_id=? AND film_id=?", userId, id);
            jdbcTemplate.update("UPDATE film SET rate=rate-1 WHERE film_id=?", id);
        }
    }

    @Override
    public List<Film> countPopularFilm(Integer count) {
        SqlRowSet filmRowsPopular = jdbcTemplate.queryForRowSet("SELECT film_id, rate FROM film GROUP BY film_id, rate ORDER BY rate DESC LIMIT ?", count);
        List<Film> AllPopularFilm = new ArrayList<>();
        while (filmRowsPopular.next()) {
            AllPopularFilm.add(getFilmId(filmRowsPopular.getInt("film_id")));
        }
        return AllPopularFilm;
    }

    @Override
    public List<Film> getFilmsByDirector(int directorId, String sortType) {
        String sqlQuery;
        List<Film> filmsSQL = new ArrayList<>();
        switch (sortType) {
            case "year":
                sqlQuery = "SELECT films_to_directors.film_id, " +
                        "film.name, " +
                        "film.description, " +
                        "film.release_date, " +
                        "film.duration, " +
                        "film.rate, " +
                        "film.rating_id, " +
                        "mpa.rating " +
                        "FROM films_to_directors " +
                        "JOIN film ON films_to_directors.film_id = film.film_id " +
                        "JOIN mpa ON film.rating_id = mpa.id " +
                        "WHERE director_id = " + directorId +
                        " ORDER BY release_date;";
                SqlRowSet filmRows = jdbcTemplate.queryForRowSet(sqlQuery);
                while (filmRows.next()) {
                    filmsSQL.add(getFilmBD(filmRows));
                }
                break;
            case "likes":
                sqlQuery = "SELECT films_to_directors.film_id, " +
                        "film.name, " +
                        "film.description, " +
                        "film.release_date, " +
                        "film.duration, " +
                        "film.rate, " +
                        "film.rating_id, " +
                        "mpa.rating " +
                        "FROM films_to_directors " +
                        "JOIN film ON films_to_directors.film_id = film.film_id " +
                        "JOIN mpa ON film.rating_id = mpa.id " +
                        "WHERE director_id = " + directorId + " " +
                        "ORDER BY rate DESC;";
                SqlRowSet filmRows1 = jdbcTemplate.queryForRowSet(sqlQuery);
                while (filmRows1.next()) {
                    filmsSQL.add(getFilmBD(filmRows1));
                }
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + sortType);
        }
        return filmsSQL;
    }

    private void additionFilm(Film film) {
        if (film.getId() == 0) {
            film.setId(++generator);
        }
    }

    private void maxId() {
        Integer i = jdbcTemplate.queryForObject("SELECT MAX(film_id) FROM film", Integer.class);
        if (i == null) {
            generator = 0;
        } else {
            generator = i;
        }
    }

    private Film getFilmBD(SqlRowSet filmRows) {
        Film filmSql = new Film();
        Integer IdFilm = filmRows.getInt("film_id");
        filmSql.setId(IdFilm);
        filmSql.setName(filmRows.getString("name"));
        filmSql.setDescription(filmRows.getString("description"));
        filmSql.setReleaseDate(filmRows.getDate("release_date").toLocalDate());
        filmSql.setDuration(filmRows.getInt("duration"));
        filmSql.setRate(filmRows.getInt("rate"));
        // Добавление в фильм рейтинг Ассоциации кинокомпаний
        filmSql.setMpa(new Rating(filmRows.getInt("rating_id"), filmRows.getString("rating")));
        // Добавление в фильм лайков
        Set<Integer> like = new HashSet<>();
        SqlRowSet userRowsLike = jdbcTemplate.queryForRowSet("SELECT user_id FROM like_users WHERE film_id=?", IdFilm);
        while (userRowsLike.next()) {
            like.add(userRowsLike.getInt("user_id"));
        }
        filmSql.setLike(like);
        // Добавление в фильм жанров
        List<Genre> genres = new ArrayList<>();
        SqlRowSet filmRowsGenre = jdbcTemplate.queryForRowSet("SELECT g.id, g.genre FROM genre AS g INNER JOIN genre_film AS gf ON g.id = gf.genre_id WHERE film_id=? GROUP BY g.id, g.genre ORDER BY g.id", IdFilm);
        while (filmRowsGenre.next()) {
            genres.add(new Genre(filmRowsGenre.getInt("id"), filmRowsGenre.getString("genre")));
        }
        filmSql.setGenres(genres);
        return filmSql;
    }
}
