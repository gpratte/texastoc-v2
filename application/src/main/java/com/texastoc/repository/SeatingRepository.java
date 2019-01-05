package com.texastoc.repository;

import com.texastoc.model.game.Seat;
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
        return jdbcTemplate
            .query("select gt.*, gs.* from gametable gt, gameseat gs where gt.gameId = :gameId and gs.gameId = gt.gameId",
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

    private static final String INSERT_TABLE_SQL = "INSERT INTO gametable "
        + " (gameId, number) "
        + " VALUES "
        + " (:gameId, :number)";
    public void save(Table table) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("gameId", table.getGameId());
        params.addValue("number", table.getNumber());

        jdbcTemplate.update(INSERT_TABLE_SQL, params);

        if (table.getSeats() != null) {
            for (Seat seat : table.getSeats()) {
                save(seat);
            }
        }
    }

    private static final String INSERT_SEAT_SQL = "INSERT INTO gameseat "
        + " (gameId, seatNumber, tableNumber, gamePlayerId, gamePlayerName) "
        + " VALUES "
        + " (:gameId, :seatNumber, :tableNumber, :gamePlayerId, :gamePlayerName)";
    public void save(Seat seat) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("gameId", seat.getGameId());
        params.addValue("seatNumber", seat.getSeatNumber());
        params.addValue("tableNumber", seat.getTableNumber());
        params.addValue("gamePlayerId", seat.getGamePlayerId());
        params.addValue("gamePlayerName", seat.getGamePlayerName());

        jdbcTemplate.update(INSERT_SEAT_SQL, params);
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
