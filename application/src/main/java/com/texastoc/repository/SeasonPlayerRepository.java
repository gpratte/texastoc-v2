package com.texastoc.repository;

import com.texastoc.model.season.SeasonPlayer;
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
public class SeasonPlayerRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Autowired
    public SeasonPlayerRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<SeasonPlayer> getBySeasonId(int id) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("seasonId", id);
        return jdbcTemplate
            .query("select * from seasonplayer"
                    + " where seasonId = :seasonId"
                    + " order by name",
                params,
                new SeasonPlayerMapper());
    }

    public void deleteBySeasonId(int seasonId) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("seasonId", seasonId);

        jdbcTemplate
            .update("delete from seasonplayer where seasonId = :seasonId", params);
    }

    private static final String INSERT_SQL =
        "INSERT INTO seasonplayer "
            + "(playerId, seasonId, name, entries, points, place) "
            + " VALUES "
            + " (:playerId, :seasonId, :name, :entries, :points, :place)";

    @SuppressWarnings("Duplicates")
    public void save(SeasonPlayer seasonPlayer) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("playerId", seasonPlayer.getPlayerId());
        params.addValue("seasonId", seasonPlayer.getSeasonId());
        params.addValue("name", seasonPlayer.getName());
        params.addValue("entries", seasonPlayer.getEntries());
        params.addValue("points", seasonPlayer.getPoints());
        params.addValue("place", seasonPlayer.getPlace());
        params.addValue("forfeit", seasonPlayer.isForfeit());

        jdbcTemplate.update(INSERT_SQL, params);
    }

    private static final class SeasonPlayerMapper implements RowMapper<SeasonPlayer> {
        @SuppressWarnings("Duplicates")
        public SeasonPlayer mapRow(ResultSet rs, int rowNum) {
            SeasonPlayer seasonPlayer = new SeasonPlayer();

            try {
                seasonPlayer.setPlayerId(rs.getInt("playerId"));
                seasonPlayer.setSeasonId(rs.getInt("seasonId"));
                seasonPlayer.setEntries(rs.getInt("entries"));
                seasonPlayer.setPoints(rs.getInt("points"));
                seasonPlayer.setPlace(rs.getInt("place"));
                seasonPlayer.setName(rs.getString("name"));
                seasonPlayer.setForfeit(rs.getBoolean("forfeit"));

            } catch (SQLException e) {
                log.error("problem mapping season player", e);
            }

            return seasonPlayer;
        }
    }

}
