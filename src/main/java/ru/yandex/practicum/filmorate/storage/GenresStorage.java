package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.ArrayList;
import java.util.Collection;

@Component
@RequiredArgsConstructor
public class GenresStorage {

    private final JdbcTemplate jdbcTemplate;

    public Collection<Genre> getAllGenres() {
        SqlRowSet genreRows = jdbcTemplate.queryForRowSet("SELECT * FROM genre");
        Collection<Genre> genreSQL = new ArrayList<>();
        while (genreRows.next()) {
            genreSQL.add(getGenreBD(genreRows));
        }
        return genreSQL;
    }

    public Genre getGenreId(Integer id) {
        SqlRowSet genreRows = jdbcTemplate.queryForRowSet("SELECT * FROM genre WHERE id=?", id);
        genreRows.next();
        if (genreRows.last()) {
            return getGenreBD(genreRows);
        } else {
            return null;
        }
    }

    private Genre getGenreBD(SqlRowSet mpaRows) {
        Genre genreSQL = new Genre();
        genreSQL.setId(mpaRows.getInt("id"));
        genreSQL.setName(mpaRows.getString("genre"));
        return genreSQL;
    }
}
