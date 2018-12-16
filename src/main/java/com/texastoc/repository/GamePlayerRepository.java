package com.texastoc.repository;

import com.texastoc.model.game.GamePlayer;
import com.texastoc.model.user.Player;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Slf4j
@Repository
public class GamePlayerRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public GamePlayerRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    public List<GamePlayer> selectByGameId(int gameId) {
        List<GamePlayer> gamePlayers = jdbcTemplate
            .query("select * from gameplayer"
                    + " where gameId = " + gameId
                    + " order by name",
                new GamePlayerMapper());

        return gamePlayers;
    }

    private static final class GamePlayerMapper implements RowMapper<GamePlayer> {
        public GamePlayer mapRow(ResultSet rs, int rowNum) {
            GamePlayer gamePlayer = new GamePlayer();
            try {
                gamePlayer.setId(rs.getInt("id"));
                gamePlayer.setPlayerId(rs.getInt("playerId"));
                gamePlayer.setGameId(rs.getInt("gameId"));
                gamePlayer.setName(rs.getString("name"));

                String value = rs.getString("buyInCollected");
                if (value != null) {
                    gamePlayer.setBuyInCollected(Integer.parseInt(value));
                }

                value = rs.getString("rebuyAddOnCollected");
                if (value != null) {
                    gamePlayer.setRebuyAddOnCollected(Integer.parseInt(value));
                }

                value = rs.getString("finish");
                if (value != null) {
                    gamePlayer.setFinish(Integer.parseInt(value));
                }

                value = rs.getString("chop");
                if (value != null) {
                    gamePlayer.setChop(Integer.parseInt(value));
                }

                value = rs.getString("points");
                if (value != null) {
                    gamePlayer.setPoints(Integer.parseInt(value));
                }

                value = rs.getString("annualTocCollected");
                if (value != null) {
                    gamePlayer.setAnnualTocCollected(Integer.parseInt(value));
                }

                value = rs.getString("quarterlyTocCollected");
                if (value != null) {
                    gamePlayer.setQuarterlyTocCollected(Integer.parseInt(value));
                }

                gamePlayer.setKnockedOut(rs.getBoolean("knockedOut"));
                gamePlayer.setRoundUpdates(rs.getBoolean("roundUpdates"));

            } catch (SQLException e) {
                log.error("problem mapping game player" , e);
            }

            return gamePlayer;
        }
    }

}
