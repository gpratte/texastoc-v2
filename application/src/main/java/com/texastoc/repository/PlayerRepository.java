package com.texastoc.repository;

import com.texastoc.model.user.Player;
import lombok.extern.slf4j.Slf4j;
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

        return jdbcTemplate.queryForObject("select * from player where id = :id", params, new PlayerMapper());
    }

    private static final String INSERT_SQL = "INSERT INTO player "
        + " (firstName, lastName, phone, email) "
        + " VALUES "
        + " (:firstName, :lastName, :phone, :email) ";
    public int save(final Player player) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("firstName", player.getFirstName());
        params.addValue("lastName", player.getLastName());
        params.addValue("phone", player.getPhone());
        params.addValue("email", player.getEmail());

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
            } catch (SQLException e) {
                log.error("Problem mapping player", e);
            }
            return player;
        }
    }

}
