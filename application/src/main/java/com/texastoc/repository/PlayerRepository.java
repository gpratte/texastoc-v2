package com.texastoc.repository;

import com.texastoc.model.user.Player;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;

@Slf4j
@Repository
public class PlayerRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public PlayerRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Player get(int id) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("id", id);

        return jdbcTemplate.query("select * from player where id = :id", params, new PlayerResultSetExtractor());
    }

    public Player getByEmail(String email) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("email", email);

        return jdbcTemplate.query("select * from player where email = :email", params, new PlayerResultSetExtractor());
    }

    private static final String UPDATE_SQL = "UPDATE player set " +
        "firstName=:firstName, lastName=:lastName, phone=:phone, " +
        "email=:email, password=:password where id=:id";
    public void update(Player player) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("firstName", player.getFirstName());
        params.addValue("lastName", player.getLastName());
        params.addValue("phone", player.getPhone());
        params.addValue("email", player.getEmail());
        params.addValue("password", player.getPassword());
        params.addValue("id", player.getId());

        jdbcTemplate.update(UPDATE_SQL, params);
    }

    private static final String INSERT_SQL = "INSERT INTO player "
        + " (firstName, lastName, phone, email, password) "
        + " VALUES "
        + " (:firstName, :lastName, :phone, :email, :password) ";
    public int save(final Player player) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("firstName", player.getFirstName());
        params.addValue("lastName", player.getLastName());
        params.addValue("phone", player.getPhone());
        params.addValue("email", player.getEmail());
        params.addValue("password", player.getPassword());

        String [] keys = {"id"};
        jdbcTemplate.update(INSERT_SQL, params, keyHolder, keys);

        //noinspection ConstantConditions
        return keyHolder.getKey().intValue();
    }

    private static final class PlayerMapper implements RowMapper<Player> {
        public Player mapRow(ResultSet rs, int rowNum) {
            Player player = new Player();
            try {
                player.setId(rs.getInt("id"));
                player.setFirstName(rs.getString("firstName"));
                player.setLastName(rs.getString("lastName"));
                player.setPhone(rs.getString("phone"));
                player.setEmail(rs.getString("email"));
                player.setPassword(rs.getString("password"));
            } catch (SQLException e) {
                log.error("Problem mapping player", e);
            }
            return player;
        }
    }

    private static final class PlayerResultSetExtractor implements ResultSetExtractor<Player> {

        @Override
        public Player extractData(ResultSet rs) throws SQLException, DataAccessException {
            Player player = new Player();

            while (rs.next()) {
                player.setId(rs.getInt("id"));
                player.setFirstName(rs.getString("firstName"));
                player.setLastName(rs.getString("lastName"));
                player.setPhone(rs.getString("phone"));
                player.setEmail(rs.getString("email"));
                player.setPassword(rs.getString("password"));
            }
            return player;
        }
    }
}
