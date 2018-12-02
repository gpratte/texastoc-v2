package com.texastoc.repository;

import com.texastoc.model.season.QuarterlySeason;
import com.texastoc.model.season.Season;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

@Repository
public class QuarterlySeasonRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Autowired
    public QuarterlySeasonRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private static final String INSERT_SQL = "INSERT INTO quarterlyseason "
        + " (seasonId, startDate, endDate, quarter, numGames, numGamesPlayed, tocCollected, tocPerGame, numPayouts) "
        + " VALUES "
        + " (:seasonId, :startDate, :endDate, :quarter, :numGames, :numGamesPlayed, :tocCollected, :tocPerGame, :numPayouts)";

    public int save(QuarterlySeason quarterlySeason) {

        KeyHolder keyHolder = new GeneratedKeyHolder();

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("seasonId", quarterlySeason.getSeasonId());
        params.addValue("startDate", quarterlySeason.getStart());
        params.addValue("endDate", quarterlySeason.getEnd());
        params.addValue("quarter", quarterlySeason.getQuarter());
        params.addValue("numGames", quarterlySeason.getNumGames());
        params.addValue("numGamesPlayed", quarterlySeason.getNumGamesPlayed());
        params.addValue("tocCollected", quarterlySeason.getTocCollected());
        params.addValue("tocPerGame", quarterlySeason.getTocPerGame());
        params.addValue("numPayouts", quarterlySeason.getNumPayouts());

        String [] keys = {"id"};
        jdbcTemplate.update(INSERT_SQL, params, keyHolder, keys);

        return keyHolder.getKey().intValue();
    }
}
