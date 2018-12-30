package com.texastoc.repository;

import com.texastoc.model.season.QuarterlySeasonPlayer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class QuarterlySeasonPlayerRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Autowired
    public QuarterlySeasonPlayerRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<QuarterlySeasonPlayer> getByQSeasonId(int qSeason) {
        throw new RuntimeException("not implemented");
    }

    public void deleteByQSeasonId(int qSeasonId) {
        throw new RuntimeException("not implemented");
    }

    public void save(QuarterlySeasonPlayer qSeasonPlayer) {
        throw new RuntimeException("not implemented");
    }

}
