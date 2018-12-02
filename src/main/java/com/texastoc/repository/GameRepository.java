package com.texastoc.repository;

import com.texastoc.model.game.Game;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class GameRepository {

    public List<Game> getBySeasonId(int seasonId) {
        return null;
    }
}
