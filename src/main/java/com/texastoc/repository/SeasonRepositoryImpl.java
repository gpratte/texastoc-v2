package com.texastoc.repository;

import com.texastoc.model.season.QuarterlySeason;
import com.texastoc.model.season.Season;
import com.texastoc.model.season.SeasonPayout;
import com.texastoc.model.season.SeasonPlayer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.time.LocalDate;
import java.util.List;

public class SeasonRepositoryImpl implements SeasonRepository {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public SeasonRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private static final String INSERT_SQL = "INSERT INTO season "
        + " (startDate, endDate, numGames, tocPerGame, kittyPerGame, quarterlyTocPerGame, quarterlyNumPayouts) "
        + " VALUES "
        + " (:startDate, :endDate, :numGamesPlayed, :tocPerGame, :kittyPerGame, :quarterlyTocPerGame, :quarterlyNumPayouts)";
    @Override
    public int save(Season season) {

        KeyHolder keyHolder = new GeneratedKeyHolder();

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("startDate", season.getStart());
        params.addValue("endDate", season.getEnd());
        params.addValue("numGames", season.getNumGames());
        params.addValue("tocPerGame", season.getTocPerGame());
        params.addValue("kittyPerGame", season.getKittyPerGame());
        params.addValue("quarterlyTocPerGame", season.getQuarterlyTocPerGame());
        params.addValue("quarterlyNumPayouts", season.getQuarterlyNumPayouts());

        String [] keys = {"id"};
        jdbcTemplate.update(INSERT_SQL, params, keyHolder, keys);

        return keyHolder.getKey().intValue();
    }
}
