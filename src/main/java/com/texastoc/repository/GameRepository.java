package com.texastoc.repository;

import com.texastoc.model.game.Game;
import com.texastoc.model.season.Quarter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

@Slf4j
@Repository
public class GameRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public GameRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Game> getBySeasonId(int seasonId) {
        return null;
    }

    private static final String INSERT_SQL =
        "INSERT INTO game "
            + "(seasonId, qSeasonId, gameDate, hostId, hostName, quarter, doubleBuyIn, transportRequired, kittyCost, buyInCost, rebuyAddOnCost, rebuyAddOnTocDebit, annualTocCost, quarterlyTocCost, numPlayers, kittyCollected, buyInCollected, rebuyAddOnCollected, annualTocCollected, quarterlyTocCollected, finalized, lastCalculated, started) "
            + " VALUES "
            + " (:seasonId, :qSeasonId, :gameDate, :hostId, :hostName, :quarter, :doubleBuyIn, :transportRequired, :kittyCost, :buyInCost, :rebuyAddOnCost, :rebuyAddOnTocDebit, :annualTocCost, :quarterlyTocCost, :numPlayers, :kittyCollected, :buyInCollected, :rebuyAddOnCollected, :annualTocCollected, :quarterlyTocCollected, :finalized, :lastCalculated, :started)";

    public int save(final Game game) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("seasonId", game.getSeasonId());
        params.addValue("qSeasonId", game.getQSeasonId());
        params.addValue("gameDate", game.getDate());
        params.addValue("hostId", game.getHostId());
        params.addValue("hostName", game.getHostName());
        params.addValue("quarter", game.getQuarter().getValue());
        params.addValue("doubleBuyIn", game.getDoubleBuyIn());
        params.addValue("transportRequired", game.getTransportRequired());
        params.addValue("kittyCost", game.getKittyCost());
        params.addValue("buyInCost", game.getBuyInCost());
        params.addValue("rebuyAddOnCost", game.getRebuyAddOnCost());
        params.addValue("rebuyAddOnTocDebit", game.getRebuyAddOnTocDebit());
        params.addValue("annualTocCost", game.getAnnualTocCost());
        params.addValue("quarterlyTocCost", game.getQuarterlyTocCost());
        params.addValue("numPlayers", game.getNumPlayers());
        params.addValue("kittyCollected", game.getKittyCollected());
        params.addValue("buyInCollected", game.getBuyInCollected());
        params.addValue("rebuyAddOnCollected", game.getRebuyAddOnCollected());
        params.addValue("annualTocCollected", game.getAnnualTocCollected());
        params.addValue("quarterlyTocCollected", game.getQuarterlyTocCollected());
        params.addValue("finalized", game.getFinalized());
        params.addValue("lastCalculated", game.getLastCalculated());
        params.addValue("started", game.getStarted() == null ? null : Timestamp.valueOf(game.getStarted()));

        String [] keys = {"id"};
        jdbcTemplate.update(INSERT_SQL, params, keyHolder, keys);

        return keyHolder.getKey().intValue();
    }


    public Game getById(int id) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("id", id);

        Game game;
        try {
            game = jdbcTemplate
                .queryForObject("select * from game where id = :id", params, new GameMapper());
        } catch(Exception e) {
            e.printStackTrace();
            return null;
        }

        return game;
    }

    private static final class GameMapper implements RowMapper<Game> {
        public Game mapRow(ResultSet rs, int rowNum) {
            Game game = new Game();
            try {
                game.setId(rs.getInt("id"));
                game.setSeasonId(rs.getInt("seasonId"));
                game.setQSeasonId(rs.getInt("qSeasonId"));
                game.setDate(rs.getDate("gameDate").toLocalDate());
                game.setQuarter(Quarter.fromInt(rs.getInt("quarter")));
                game.setDoubleBuyIn(rs.getBoolean("doubleBuyIn"));
                game.setTransportRequired(rs.getBoolean("transportRequired"));
                game.setKittyCost(rs.getInt("kittyCost"));
                game.setBuyInCost(rs.getInt("buyInCost"));
                game.setRebuyAddOnCost(rs.getInt("rebuyAddOnCost"));
                game.setRebuyAddOnTocDebit(rs.getInt("rebuyAddOnTocDebit"));
                game.setAnnualTocCost(rs.getInt("annualTocCost"));
                game.setQuarterlyTocCost(rs.getInt("quarterlyTocCost"));
                game.setNumPlayers(rs.getInt("numPlayers"));
                game.setKittyCollected(rs.getInt("kittyCollected"));
                game.setBuyInCollected(rs.getInt("buyInCollected"));
                game.setRebuyAddOnCollected(rs.getInt("rebuyAddOnCollected"));
                game.setAnnualTocCollected(rs.getInt("annualTocCollected"));
                game.setQuarterlyTocCollected(rs.getInt("quarterlyTocCollected"));
                game.setFinalized(rs.getBoolean("finalized"));

                String value = rs.getString("hostId");
                if (value != null) {
                    game.setHostId(rs.getInt("hostId"));
                }
                value = rs.getString("hostName");
                if (value != null) {
                    game.setHostName(rs.getString("hostName"));
                }

                Timestamp time = rs.getTimestamp("started");
                if (time != null) {
                    game.setStarted(time.toLocalDateTime());
                }

                time = rs.getTimestamp("lastCalculated");
                if (time != null) {
                    game.setLastCalculated(time.toLocalDateTime());
                }

            } catch (SQLException e) {
                log.error("Problem mapping game", e);
            }

            return game;
        }
    }

}
