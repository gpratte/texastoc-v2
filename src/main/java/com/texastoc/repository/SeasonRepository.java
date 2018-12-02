package com.texastoc.repository;

import com.texastoc.model.season.Season;
import org.springframework.stereotype.Repository;

@Repository
public interface SeasonRepository {

    public int save(Season season);
}
