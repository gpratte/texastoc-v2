package com.texastoc.repository;

import com.texastoc.model.season.SeasonPayout;
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
public class SeasonPayoutRepository {

  private final NamedParameterJdbcTemplate jdbcTemplate;

  public SeasonPayoutRepository(NamedParameterJdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }


  public List<SeasonPayout> getBySeasonId(int id) {
    MapSqlParameterSource params = new MapSqlParameterSource();
    params.addValue("id", id);

    return jdbcTemplate
        .query("select * from seasonpayout where seasonId = :id order by amount desc",
            params,
            new SeasonPayoutMapper());
  }

  private static final String INSERT_SQL =
      "INSERT INTO seasonpayout "
          + "(seasonId, place, amount) "
          + " VALUES "
          + " (:seasonId, :place, :amount)";

  @SuppressWarnings("Duplicates")
  public int save(final SeasonPayout payout) {
    KeyHolder keyHolder = new GeneratedKeyHolder();

    MapSqlParameterSource params = new MapSqlParameterSource();
    params.addValue("seasonId", payout.getSeasonId());
    params.addValue("place", payout.getPlace());
    params.addValue("amount", payout.getAmount());

    String[] keys = {"id"};
    jdbcTemplate.update(INSERT_SQL, params, keyHolder, keys);
    return keyHolder.getKey().intValue();
  }

  public void deleteBySeasonId(int id) {
    MapSqlParameterSource params = new MapSqlParameterSource();
    params.addValue("id", id);

    jdbcTemplate.update("delete from seasonpayout where seasonId = :id", params);
  }

  private static final class SeasonPayoutMapper implements RowMapper<SeasonPayout> {
    public SeasonPayout mapRow(ResultSet rs, int rowNum) {
      SeasonPayout seasonPayout = new SeasonPayout();
      try {
        seasonPayout.setId(rs.getInt("id"));
        seasonPayout.setSeasonId(rs.getInt("seasonId"));
        seasonPayout.setPlace(rs.getInt("place"));
        seasonPayout.setAmount(rs.getInt("amount"));
      } catch (SQLException e) {
        log.error("Problem mapping GamePayout", e);
      }

      return seasonPayout;
    }
  }

}
