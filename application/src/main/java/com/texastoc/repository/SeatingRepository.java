package com.texastoc.repository;

import com.texastoc.model.game.Table;
import com.texastoc.model.supply.Supply;
import com.texastoc.model.supply.SupplyType;
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
public class SeatingRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public SeatingRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Table> get(int gameId) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("gameId", gameId);
        // TODO outer join on gameseat
        return jdbcTemplate
            .query("select * from gametable where gameId = :gameId",
                params,
                new TableMapper());
    }


    public void deleteByGameId(int gameId) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("gameId", gameId);

        jdbcTemplate
            .update("delete from gametable where gameId = :gameId", params);
        jdbcTemplate
            .update("delete from gameseat where gameId = :gameId", params);
    }

    private static final String INSERT_SQL = "INSERT INTO supply "
        + " (date, type, amount, description) "
        + " VALUES "
        + " (:date, :type, :amount, :description)";
    public void save(Table table) {
//        MapSqlParameterSource params = new MapSqlParameterSource();
//        params.addValue("date", supply.getDate());
//        params.addValue("type", supply.getType().name());
//        params.addValue("amount", supply.getAmount());
//        params.addValue("description", supply.getDescription());
//
//        jdbcTemplate.update(INSERT_SQL, params);
    }

    private static final class TableMapper implements RowMapper<Table> {
        public Table mapRow(ResultSet rs, int rowNum) {
            Table table = new Table();
            try {
                table.setGameId(rs.getInt("gameId"));
                table.setNumber(rs.getInt("number"));
            } catch (SQLException e) {
                log.error("Problem mapping Table", e);
            }

            return table;
        }
    }

}
