package com.texastoc.repository;

import com.texastoc.model.game.GamePayout;
import com.texastoc.model.season.SeasonPayout;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Slf4j
@Repository
public class SeasonPayoutRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public SeasonPayoutRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    public List<SeasonPayout> getByQuarterlySeasonId(int id) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("id", id);

        return jdbcTemplate
            .query("select * from seasonpayout where qSeasonId = :id order by amount desc",
                params,
                new SeasonPayoutMapper());
    }

//    private static final String INSERT_SQL =
//        "INSERT INTO gamepayout "
//            + "(gameId, place, amount, chopAmount, chopPercent) "
//            + " VALUES "
//            + " (:gameId, :place, :amount, :chopAmount, :chopPercent)";
//    public void save(final GamePayout gamePayout) {
//
//        MapSqlParameterSource params = new MapSqlParameterSource();
//        params.addValue("gameId", gamePayout.getGameId());
//        params.addValue("place", gamePayout.getPlace());
//        params.addValue("amount", gamePayout.getAmount());
//        params.addValue("chopAmount", gamePayout.getChopAmount());
//        params.addValue("chopPercent", gamePayout.getChopPercent());
//
//        jdbcTemplate.update(INSERT_SQL, params);
//    }
//
//
//    public void deleteByGameId(int id) {
//        MapSqlParameterSource params = new MapSqlParameterSource();
//        params.addValue("id", id);
//
//        jdbcTemplate.update("delete from gamepayout where gameId = :id", params);
//    }

    private static final class SeasonPayoutMapper implements RowMapper<SeasonPayout> {
        public SeasonPayout mapRow(ResultSet rs, int rowNum) {
            SeasonPayout seasonPayout = new SeasonPayout();
            try {
                seasonPayout.setId(rs.getInt("id"));
                seasonPayout.setSeasonId(rs.getInt("seasonId"));
                seasonPayout.setQSeasonId(rs.getInt("qSeasonId"));
                seasonPayout.setPlace(rs.getInt("place"));
                seasonPayout.setAmount(rs.getInt("amount"));
            } catch (SQLException e) {
                log.error("Problem mapping GamePayout", e);
            }

            return seasonPayout;
        }
    }

}
