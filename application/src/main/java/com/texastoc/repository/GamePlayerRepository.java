package com.texastoc.repository;

import com.texastoc.model.game.Game;
import com.texastoc.model.game.GamePlayer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

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

    public GamePlayer selectById(int id) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("id", id);

        GamePlayer gamePlayer;
        try {
            gamePlayer = jdbcTemplate
                .queryForObject("select * from gameplayer where id = :id", params, new GamePlayerMapper());
        } catch(Exception e) {
            e.printStackTrace();
            return null;
        }

        return gamePlayer;
    }

    private static final String INSERT_SQL =
        "INSERT INTO gameplayer "
            + "(playerId, gameId, name, points, finish, knockedOut, roundUpdates, buyInCollected, rebuyAddOnCollected, annualTocCollected, quarterlyTocCollected, chop) "
            + " VALUES "
            + " (:playerId, :gameId, :name, :points, :finish, :knockedOut, :roundUpdates, :buyInCollected, :rebuyAddOnCollected, :annualTocCollected, :quarterlyTocCollected, :chop)";

    public int save(final GamePlayer gamePlayer) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("playerId", gamePlayer.getPlayerId());
        params.addValue("gameId", gamePlayer.getGameId());
        params.addValue("name", gamePlayer.getName());
        params.addValue("points", gamePlayer.getPoints());
        params.addValue("finish", gamePlayer.getFinish());
        params.addValue("knockedOut", gamePlayer.getKnockedOut());
        params.addValue("roundUpdates", gamePlayer.getRoundUpdates());
        params.addValue("buyInCollected", gamePlayer.getBuyInCollected());
        params.addValue("rebuyAddOnCollected", gamePlayer.getRebuyAddOnCollected());
        params.addValue("annualTocCollected", gamePlayer.getAnnualTocCollected());
        params.addValue("quarterlyTocCollected", gamePlayer.getQuarterlyTocCollected());
        params.addValue("knockedOut", gamePlayer.getKnockedOut());
        params.addValue("chop", gamePlayer.getChop());

        String [] keys = {"id"};
        jdbcTemplate.update(INSERT_SQL, params, keyHolder, keys);

        return keyHolder.getKey().intValue();
    }


    private static final String UPDATE_SQL = "UPDATE gameplayer set " +
        "playerId=:playerId, gameId=:gameId, name=:name, " +
        "points=:points, finish=:finish, knockedOut=:knockedOut, " +
        "roundUpdates=:roundUpdates, buyInCollected=:buyInCollected, " +
        "rebuyAddOnCollected=:rebuyAddOnCollected, annualTocCollected=:annualTocCollected, " +
        "quarterlyTocCollected=:quarterlyTocCollected, chop=:chop " +
        " where id=:id";

    public void update(final GamePlayer player) {

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("playerId", player.getPlayerId());
        params.addValue("gameId", player.getGameId());
        params.addValue("name", player.getName());
        params.addValue("points", player.getPoints());
        params.addValue("finish", player.getFinish());
        params.addValue("knockedOut", player.getKnockedOut());
        params.addValue("roundUpdates", player.getRoundUpdates());
        params.addValue("buyInCollected", player.getBuyInCollected());
        params.addValue("rebuyAddOnCollected", player.getRebuyAddOnCollected());
        params.addValue("annualTocCollected", player.getAnnualTocCollected());
        params.addValue("quarterlyTocCollected", player.getQuarterlyTocCollected());
        params.addValue("chop", player.getChop());

        params.addValue("id", player.getId());

        jdbcTemplate.update(UPDATE_SQL, params);
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

                value = rs.getString("quarterlyTocCollected");
                if (value != null) {
                    gamePlayer.setQuarterlyTocCollected(Integer.parseInt(value));
                }

                value = rs.getString("knockedOut");
                if (value != null) {
                    gamePlayer.setKnockedOut(rs.getBoolean("knockedOut"));
                }

                value = rs.getString("roundUpdates");
                if (value != null) {
                    gamePlayer.setRoundUpdates(rs.getBoolean("roundUpdates"));
                }

            } catch (SQLException e) {
                log.error("problem mapping game player" , e);
            }

            return gamePlayer;
        }
    }

}
