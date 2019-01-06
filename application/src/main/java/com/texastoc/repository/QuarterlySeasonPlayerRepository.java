package com.texastoc.repository;

import com.texastoc.model.season.QuarterlySeasonPlayer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Slf4j
@Repository
public class QuarterlySeasonPlayerRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Autowired
    public QuarterlySeasonPlayerRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<QuarterlySeasonPlayer> getByQSeasonId(int qSeasonId) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("qSeasonId", qSeasonId);
        return jdbcTemplate
            .query("select * from quarterlyseasonplayer"
                    + " where qSeasonId = :qSeasonId"
                    + " order by name",
                params,
                new QuarterlySeasonPlayerMapper());
    }

    public void deleteByQSeasonId(int qSeasonId) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("qSeasonId", qSeasonId);

        jdbcTemplate
            .update("delete from quarterlyseasonplayer where qSeasonId = :qSeasonId", params);
    }

    private static final String INSERT_SQL =
        "INSERT INTO quarterlyseasonplayer "
            + "(playerId, seasonId, qSeasonId, name, entries, points, place) "
            + " VALUES "
            + " (:playerId, :seasonId, :qSeasonId, :name, :entries, :points, :place)";

    @SuppressWarnings("Duplicates")
    public void save(QuarterlySeasonPlayer qSeasonPlayer) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("playerId", qSeasonPlayer.getPlayerId());
        params.addValue("seasonId", qSeasonPlayer.getSeasonId());
        params.addValue("qSeasonId", qSeasonPlayer.getQSeasonId());
        params.addValue("name", qSeasonPlayer.getName());
        params.addValue("entries", qSeasonPlayer.getEntries());
        params.addValue("points", qSeasonPlayer.getPoints());
        params.addValue("place", qSeasonPlayer.getPlace());

        jdbcTemplate.update(INSERT_SQL, params);
    }

    private static final class QuarterlySeasonPlayerMapper implements RowMapper<QuarterlySeasonPlayer> {
        @SuppressWarnings("Duplicates")
        public QuarterlySeasonPlayer mapRow(ResultSet rs, int rowNum) {
            QuarterlySeasonPlayer qSeasonPlayer = new QuarterlySeasonPlayer();

            try {
                qSeasonPlayer.setPlayerId(rs.getInt("playerId"));
                qSeasonPlayer.setSeasonId(rs.getInt("seasonId"));
                qSeasonPlayer.setQSeasonId(rs.getInt("qSeasonId"));
                qSeasonPlayer.setEntries(rs.getInt("entries"));
                qSeasonPlayer.setPoints(rs.getInt("points"));
                qSeasonPlayer.setPlace(rs.getInt("place"));

                qSeasonPlayer.setName(rs.getString("name"));

            } catch (SQLException e) {
                log.error("problem mapping quarterly season player", e);
            }

            return qSeasonPlayer;
        }
    }
}