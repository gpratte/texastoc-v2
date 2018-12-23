package com.texastoc.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

@Slf4j
@Repository
public class PayoutRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public PayoutRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private static HashMap<Integer, HashMap<Integer,Double>> PAYOUTS =
        new HashMap<>();


    public HashMap<Integer,Double> get(int num) {

        if (PAYOUTS.get(num) != null) {
            return PAYOUTS.get(num);
        }

        // Get all the payouts and cache them in PAYOUTS
        MapSqlParameterSource params = new MapSqlParameterSource();

        List<Payout> payouts = jdbcTemplate.query("select * from payout", params, new PayoutMapper());

        for (Payout payout : payouts) {
            HashMap<Integer, Double> percent = PAYOUTS.get(payout.numPayouts);
            if (percent == null) {
                percent = new HashMap<Integer, Double>();
                PAYOUTS.put(payout.numPayouts, percent);
            }
            percent.put(payout.place, payout.percent);
        }

        return PAYOUTS.get(num);
    }

    private static final class Payout {
        int numPayouts;
        int place;
        double percent;
    }

    private static final class PayoutMapper implements RowMapper<Payout> {
        public Payout mapRow(ResultSet rs, int rowNum) {
            Payout payout = new Payout();
            try {
                payout.numPayouts = rs.getInt("numPayouts");
                payout.place = rs.getInt("place");
                payout.percent = rs.getDouble("percent");
            } catch (SQLException e) {
                log.error("Problem mapping TocConfig", e);
            }
            return payout;
        }
    }
}
