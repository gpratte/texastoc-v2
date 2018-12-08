package com.texastoc.repository;

import com.texastoc.model.season.Season;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.time.LocalDate;

@Slf4j
@Repository
public class SeasonRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Autowired
    public SeasonRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private static final String INSERT_SQL = "INSERT INTO season "
        + " (startDate, endDate, finalized, numGames, numGamesPlayed, totalBuyIn, totalReBuy, totalAnnualToc, annualTocAmount, kittyPerGame, quarterlyTocAmount, quarterlyTocPayouts) "
        + " VALUES "
        + " (:startDate, :endDate, :finalized, :numGames, :numGamesPlayed, :totalBuyIn, :totalReBuy, :totalAnnualToc, :annualTocAmount, :kittyPerGame, :quarterlyTocAmount, :quarterlyTocPayouts)";

    public int save(Season season) {

        KeyHolder keyHolder = new GeneratedKeyHolder();

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("startDate", season.getStart());
        params.addValue("endDate", season.getEnd());
        params.addValue("finalized", season.getFinalized());
        params.addValue("numGames", season.getNumGames());
        params.addValue("numGamesPlayed", season.getNumGamesPlayed());
        params.addValue("totalBuyIn", season.getBuyInCollected());
        params.addValue("totalReBuy", season.getRebuyAddOnCollected());
        params.addValue("totalAnnualToc", season.getTocCollected());
        params.addValue("annualTocAmount", season.getTocPerGame());
        params.addValue("kittyPerGame", season.getKittyPerGame());
        params.addValue("quarterlyTocAmount", season.getQuarterlyTocPerGame());
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
                .queryForObject("select * from season where id = 1", params, new SeasonMapper());
        } catch(Exception e) {
            return null;
        }
    }

    public Season getCurrent() {
        return null;
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
                season.setBuyInCollected(rs.getInt("totalBuyIn"));
                season.setRebuyAddOnCollected(rs.getInt("totalReBuy"));
                season.setTocCollected(rs.getInt("totalAnnualToc"));
                season.setTocPerGame(rs.getInt("annualTocAmount"));
                season.setKittyPerGame(rs.getInt("kittyPerGame"));
                season.setQuarterlyTocPerGame(rs.getInt("quarterlyTocAmount"));
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
