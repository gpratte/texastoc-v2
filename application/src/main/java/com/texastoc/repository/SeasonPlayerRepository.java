package com.texastoc.repository;

import com.texastoc.model.season.SeasonPlayer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class SeasonPlayerRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Autowired
    public SeasonPlayerRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<SeasonPlayer> getBySeasonId(int id) {
        return null;
    }

    public List<SeasonPlayer> getByQuarterlySeasonId(int id) {
        return null;
    }

}
