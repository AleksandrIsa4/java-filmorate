package ru.yandex.practicum.filmorate.storage;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.director.DirectorStorage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Component
public class DirectorDbStorage implements DirectorStorage {
    private final JdbcTemplate jdbcTemplate;

    public DirectorDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Director> findAll() {
        String sqlQuery = "SELECT * FROM directors;";
        return jdbcTemplate.query(sqlQuery, (resultSet, rowNum) -> makeDirector(resultSet));
    }

    @Override
    public Optional<Director> find(int id) {
        String sqlQuery = "SELECT * FROM directors WHERE director_id = " + id + ";";

        return jdbcTemplate.query(sqlQuery, (resultSet, rowNum) -> makeDirector(resultSet))
                .stream()
                .findAny();
    }

    @Override
    public Director add(Director director) {
        String sqlQuery = "INSERT INTO directors (name) VALUES (?);";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(sqlQuery, new String[]{"director_id"});
            statement.setString(1, director.getName());
            return statement;
        }, keyHolder);
        director.setId(Objects.requireNonNull(keyHolder.getKey()).intValue());

        return director;
    }

    @Override
    public Director update(Director director) {
        String sqlQuery = "UPDATE directors SET name = ? WHERE director_id = ?;";
        jdbcTemplate.update(sqlQuery,
                director.getName(),
                director.getId());

        return director;
    }

    @Override
    public Director delete(int id) {
        String sqlQuery = "DELETE FROM directors WHERE director_id = ? ;";
        jdbcTemplate.update(sqlQuery, id);

        return null;
    }

    @Override
    public void addToFilm(int filmId, int directorId) {
        String sqlQuery = "INSERT INTO films_to_directors VALUES (?,?);";
        jdbcTemplate.update(sqlQuery, filmId, directorId);
    }

    @Override
    public void deleteAllByFilmId(int filmId) {
        String sqlQuery = "DELETE FROM films_to_directors WHERE film_id = ?;";
        jdbcTemplate.update(sqlQuery, filmId);
    }

    @Override
    public List<Director> findAllToFilm(int filmId) {
        String sqlQuery = "SELECT films_to_directors.director_id," +
                "directors.name FROM films_to_directors " +
                "JOIN directors ON films_to_directors.director_id = directors.director_id " +
                "WHERE film_id = " + filmId + ";";

        return jdbcTemplate.query(sqlQuery, (resultSet, rowNum) -> makeDirector(resultSet));
    }

    @Override
    public Map<Integer, List<Director>> findAllToFilm() {
        String sqlQuery = "SELECT films_to_directors.film_id, " +
                "films_to_directors.director_id, " +
                "directors.name " +
                "FROM films_to_directors " +
                "JOIN directors ON films_to_directors.director_id = directors.director_id;";

        return jdbcTemplate.query(sqlQuery, this::makeDirectorsToFilm);
    }

    @Override
    public boolean isAlreadyExist(int id) {
        String sqlQuery = "SELECT director_id FROM directors;";

        return jdbcTemplate.queryForList(sqlQuery, Integer.class)
                .contains(id);
    }

    private Map<Integer, List<Director>> makeDirectorsToFilm(ResultSet resultSet) throws SQLException {
        Map<Integer, List<Director>> directorToFilm = new LinkedHashMap<>();
        while(resultSet.next()) {
            Integer filmId = resultSet.getInt("film_id");
            directorToFilm.putIfAbsent(filmId, new ArrayList<>());
            int directorId = resultSet.getInt("director_id");
            String name = resultSet.getString("name");
            directorToFilm.get(filmId).add(new Director(directorId, name));
        }

        return directorToFilm;
    }

    private Director makeDirector(ResultSet resultSet) throws SQLException {
        int id = resultSet.getInt("director_id");
        String name = resultSet.getString("name");

        return new Director(id, name);
    }
}
