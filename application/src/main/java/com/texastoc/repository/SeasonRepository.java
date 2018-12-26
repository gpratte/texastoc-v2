package com.texastoc.repository;

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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

@Slf4j
@Repository
public class SeasonRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Autowired
    public SeasonRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private static final String INSERT_SQL = "INSERT INTO season "
        + " (startDate, endDate, finalized, numGames, numGamesPlayed, buyInCost, rebuyAddOnCost, rebuyAddOnTocDebit, doubleBuyInCost, doubleRebuyAddOnCost, doubleRebuyAddOnTocDebit, buyInCollected, rebuyAddOnCollected, tocCollected, tocPerGame, kittyPerGame, quarterlyTocPerGame, quarterlyTocPayouts) "
        + " VALUES "
        + " (:startDate, :endDate, :finalized, :numGames, :numGamesPlayed, :buyInCost, :rebuyAddOnCost, :rebuyAddOnTocDebit, :doubleBuyInCost, :doubleRebuyAddOnCost, :doubleRebuyAddOnTocDebit, :buyInCollected, :rebuyAddOnCollected, :tocCollected, :tocPerGame, :kittyPerGame, :quarterlyTocPerGame, :quarterlyTocPayouts)";

    public int save(Season season) {

        KeyHolder keyHolder = new GeneratedKeyHolder();

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("startDate", season.getStart());
        params.addValue("endDate", season.getEnd());
        params.addValue("finalized", season.getFinalized());
        params.addValue("numGames", season.getNumGames());
        params.addValue("numGamesPlayed", season.getNumGamesPlayed());
        params.addValue("buyInCost", season.getBuyInCost());
        params.addValue("rebuyAddOnCost", season.getRebuyAddOnCost());
        params.addValue("rebuyAddOnTocDebit", season.getRebuyAddOnTocDebit());
        params.addValue("doubleBuyInCost", season.getDoubleBuyInCost());
        params.addValue("doubleRebuyAddOnCost", season.getDoubleRebuyAddOnCost());
        params.addValue("doubleRebuyAddOnTocDebit", season.getDoubleRebuyAddOnTocDebit());
        params.addValue("buyInCollected", season.getBuyInCollected());
        params.addValue("rebuyAddOnCollected", season.getRebuyAddOnCollected());
        params.addValue("tocCollected", season.getTocCollected());
        params.addValue("tocPerGame", season.getTocPerGame());
        params.addValue("kittyPerGame", season.getKittyPerGame());
        params.addValue("quarterlyTocPerGame", season.getQuarterlyTocPerGame());
        params.addValue("quarterlyTocPayouts", season.getQuarterlyNumPayouts());

        String [] keys = {"id"};
        jdbcTemplate.update(INSERT_SQL, params, keyHolder, keys);

        return keyHolder.getKey().intValue();
    }

    public Season get(int id) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("id", id);

        try {
            return jdbcTemplate
                .queryForObject("select * from season where id = :id", params, new SeasonMapper());
        } catch(Exception e) {
            return null;
        }
    }

    public Season getCurrent() {
        MapSqlParameterSource params = new MapSqlParameterSource();
        // This is a bit of a hack. Ideally there would only be one current season. But the tests create multiple seasons in the same date range. Hence get all the seasons that encompass the date and take the lastest one (the one with the highest id).
        List<Season> seasons = jdbcTemplate.query("select * from season where CURRENT_DATE >= startDate and CURRENT_DATE <= endDate order by id desc", params, new SeasonMapper());

        if (seasons.size() > 0) {
            return seasons.get(0);
        }

        throw new IncorrectResultSizeDataAccessException(0);
    }

    private static final class SeasonMapper implements RowMapper<Season> {
        public Season mapRow(ResultSet rs, int rowNum) {
            Season season = new Season();
            try {
                season.setId(rs.getInt("id"));
                season.setStart(rs.getDate("startDate").toLocalDate());
                season.setEnd(rs.getDate("endDate").toLocalDate());
                season.setFinalized(rs.getBoolean("finalized"));
                season.setNumGames(rs.getInt("numGames"));
                season.setNumGamesPlayed(rs.getInt("numGamesPlayed"));
                season.setBuyInCost(rs.getInt("buyInCost"));
                season.setRebuyAddOnCost(rs.getInt("rebuyAddOnCost"));
                season.setRebuyAddOnTocDebit(rs.getInt("rebuyAddOnTocDebit"));
                season.setDoubleBuyInCost(rs.getInt("doubleBuyInCost"));
                season.setDoubleRebuyAddOnCost(rs.getInt("doubleRebuyAddOnCost"));
                season.setDoubleRebuyAddOnTocDebit(rs.getInt("doubleRebuyAddOnTocDebit"));
                season.setTocPerGame(rs.getInt("tocPerGame"));
                season.setKittyPerGame(rs.getInt("kittyPerGame"));
                season.setBuyInCollected(rs.getInt("buyInCollected"));
                season.setRebuyAddOnCollected(rs.getInt("rebuyAddOnCollected"));
                season.setTocCollected(rs.getInt("tocCollected"));
                season.setQuarterlyTocPerGame(rs.getInt("quarterlyTocPerGame"));
                season.setQuarterlyNumPayouts(rs.getInt("quarterlyTocPayouts"));

                Timestamp lastCalculated = rs.getTimestamp("lastCalculated");
                if (lastCalculated != null) {
                    season.setLastCalculated(lastCalculated.toLocalDateTime());
                }
            } catch (SQLException e) {
                log.error("Error mapping table to object", e);
            }
            return season;
        }
    }

}
