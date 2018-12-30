package com.texastoc.repository;

import com.texastoc.model.game.GamePlayer;
import com.texastoc.model.season.QuarterlySeasonPlayer;
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
        throw new RuntimeException("not implemented");
    }

    public void save(QuarterlySeasonPlayer qSeasonPlayer) {
        throw new RuntimeException("not implemented");
    }

    private static final class QuarterlySeasonPlayerMapper implements RowMapper<QuarterlySeasonPlayer> {
        public QuarterlySeasonPlayer mapRow(ResultSet rs, int rowNum) {
            QuarterlySeasonPlayer qSeasonPlayer = new QuarterlySeasonPlayer();

            try {
                qSeasonPlayer.setPlayerId(rs.getInt("playerId"));
                qSeasonPlayer.setSeasonId(rs.getInt("seasonId"));
                qSeasonPlayer.setQSeasonId(rs.getInt("qSeasonId"));
                qSeasonPlayer.setEntries(rs.getInt("entries"));
                qSeasonPlayer.setPoints(rs.getInt("points"));

                qSeasonPlayer.setName(rs.getString("name"));

                String value = rs.getString("place");
                if (value != null) {
                    qSeasonPlayer.setPlace(Integer.parseInt(value));
                }

            } catch (SQLException e) {
                log.error("problem mapping quarterly season player", e);
            }

            return qSeasonPlayer;
        }
    }
}