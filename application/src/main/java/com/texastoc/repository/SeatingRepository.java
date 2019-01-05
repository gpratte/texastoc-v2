package com.texastoc.repository;

import com.texastoc.model.game.Seat;
import com.texastoc.model.game.Table;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Repository
public class SeatingRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public SeatingRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Seat> get(int gameId) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("gameId", gameId);
        return jdbcTemplate
            .query("select * from gameseat where gameId = :gameId order by tableNumber, seatNumber",
                params,
                new SeatMapper());
    }


    public List<Table> getTables(int gameId) {
        List<Seat> seats = get(gameId);
        Map<Integer, Table> tableMap = new HashMap<>();
        for (Seat seat : seats) {
            Table table = tableMap.get(seat.getTableNumber());
            if (table == null) {
                table = new Table();
                table.setGameId(seat.getGameId());
                table.setNumber(seat.getTableNumber());
                tableMap.put(seat.getTableNumber(), table);
            }
            table.addSeat(seat);
        }

        return new ArrayList<>(tableMap.values());
    }

    public void deleteByGameId(int gameId) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("gameId", gameId);

        jdbcTemplate
            .update("delete from gameseat where gameId = :gameId", params);
    }

    private static final String INSERT_SEAT_SQL = "INSERT INTO gameseat "
        + " (gameId, seatNumber, tableNumber, gamePlayerId, gamePlayerName) "
        + " VALUES "
        + " (:gameId, :seatNumber, :tableNumber, :gamePlayerId, :gamePlayerName)";
    public void save(List<Seat> seats) {

        for (Seat seat : seats) {
            MapSqlParameterSource params = new MapSqlParameterSource();
            params.addValue("gameId", seat.getGameId());
            params.addValue("seatNumber", seat.getSeatNumber());
            params.addValue("tableNumber", seat.getTableNumber());
            params.addValue("gamePlayerId", seat.getGamePlayerId());
            params.addValue("gamePlayerName", seat.getGamePlayerName());

            jdbcTemplate.update(INSERT_SEAT_SQL, params);
        }
    }

    public void saveTable(Table table) {
        this.save(table.getSeats());
    }

    private static final class SeatMapper implements RowMapper<Seat> {
        public Seat mapRow(ResultSet rs, int rowNum) {

            Seat seat = null;
            try {
                seat = new Seat();
                seat.setGameId(rs.getInt("gameId"));
                seat.setSeatNumber(rs.getInt("seatNumber"));
                seat.setTableNumber(rs.getInt("tableNumber"));

                String value = rs.getString("gamePlayerId");
                if (value != null) {
                    seat.setGamePlayerId(Integer.parseInt(value));
                }

                value = rs.getString("gamePlayerName");
                if (value != null) {
                    seat.setGamePlayerName(value);
                }

            } catch (SQLException e) {
                log.error("Problem mapping Seat", e);
            }

            return seat;
        }
    }

}
