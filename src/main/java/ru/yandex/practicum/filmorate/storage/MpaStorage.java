package ru.yandex.practicum.filmorate.storage;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Rating;

import java.util.ArrayList;
import java.util.Collection;

@Component
public class MpaStorage {

    private final JdbcTemplate jdbcTemplate;

    public MpaStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Collection<Rating> getAllMpa() {
        SqlRowSet mpaRows = jdbcTemplate.queryForRowSet("SELECT * FROM mpa");
        Collection<Rating> mpaSQL = new ArrayList<>();
        while (mpaRows.next()) {
            mpaSQL.add(getMpaBD(mpaRows));
        }
        return mpaSQL;
    }

    public Rating getMpaId(Integer id) {
        SqlRowSet mpaRows = jdbcTemplate.queryForRowSet("SELECT * FROM mpa WHERE id=?", id);
        mpaRows.next();
        if (mpaRows.last()) {
            return getMpaBD(mpaRows);
        } else {
            return null;
        }
    }

    private Rating getMpaBD(SqlRowSet mpaRows) {
        Rating mpaSQL = new Rating();
        mpaSQL.setId(mpaRows.getInt("id"));
        mpaSQL.setName(mpaRows.getString("rating"));
        return mpaSQL;
    }
}
