package com.texastoc.repository;

import com.texastoc.model.game.Game;
import com.texastoc.model.season.Quarter;
import com.texastoc.model.season.QuarterlySeason;
import com.texastoc.model.season.Season;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Clock;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;

@Slf4j
@Repository
public class QuarterlySeasonRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Autowired
    public QuarterlySeasonRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private static final String INSERT_SQL = "INSERT INTO quarterlyseason "
        + " (seasonId, startDate, endDate, finalized, quarter, numGames, numGamesPlayed, totalQuarterlyToc, tocPerGame, numPayouts) "
        + " VALUES "
        + " (:seasonId, :startDate, :endDate, :finalized, :quarter, :numGames, :numGamesPlayed, :totalQuarterlyToc, :tocPerGame, :numPayouts)";

    public int save(QuarterlySeason quarterlySeason) {

        KeyHolder keyHolder = new GeneratedKeyHolder();

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("seasonId", quarterlySeason.getSeasonId());
        params.addValue("startDate", quarterlySeason.getStart());
        params.addValue("endDate", quarterlySeason.getEnd());
        params.addValue("finalized", false);
        params.addValue("quarter", quarterlySeason.getQuarter().getValue());
        params.addValue("numGames", quarterlySeason.getNumGames());
        params.addValue("numGamesPlayed", quarterlySeason.getNumGamesPlayed());
        params.addValue("totalQuarterlyToc", quarterlySeason.getTocCollected());
        params.addValue("tocPerGame", quarterlySeason.getTocPerGame());
        params.addValue("numPayouts", quarterlySeason.getNumPayouts());

        String[] keys = {"id"};
        jdbcTemplate.update(INSERT_SQL, params, keyHolder, keys);

        return keyHolder.getKey().intValue();
    }

    public List<QuarterlySeason> getBySeasonId(int seasonId) {
        return jdbcTemplate.query("select * from quarterlyseason "
                    + " where seasonId=" + seasonId + " order by quarter",
                new QuarterlySeasonMapper());
    }

    public QuarterlySeason getCurrent() {
        MapSqlParameterSource params = new MapSqlParameterSource();
        // This is a bit of a hack. Ideally there would only be one current season. But the tests create multiple quarterly seasons in the same date range. Hence get all the quarterly seasons that encompass the date and take the lastest one.

        List<QuarterlySeason> qSeasons = jdbcTemplate.query("select * from quarterlyseason where CURRENT_DATE >= startDate and CURRENT_DATE <= endDate order by startDate desc", params, new QuarterlySeasonMapper());

        if (qSeasons.size() > 0) {
            return qSeasons.get(0);
        }

        throw new IncorrectResultSizeDataAccessException(0);
    }

    private static final class QuarterlySeasonMapper implements RowMapper<QuarterlySeason> {
        public QuarterlySeason mapRow(ResultSet rs, int rowNum) {
            QuarterlySeason quarterly = new QuarterlySeason();
            try {
                quarterly.setId(rs.getInt("id"));
                quarterly.setSeasonId(rs.getInt("seasonId"));
                quarterly.setStart(rs.getDate("startDate").toLocalDate());
                quarterly.setEnd(rs.getDate("endDate").toLocalDate());
                quarterly.setTocCollected(rs.getInt("totalQuarterlyToc"));

                Timestamp lastCalculated = rs.getTimestamp("lastCalculated");
                if (lastCalculated != null) {
                    quarterly.setLastCalculated(lastCalculated.toLocalDateTime());
                }

                int quarterlyValue = rs.getInt("quarter");
                quarterly.setQuarter(Quarter.fromInt(quarterlyValue));
            } catch (SQLException e) {
                log.error("Problem mapping repository", e);
            }

            return quarterly;
        }
    }

}
