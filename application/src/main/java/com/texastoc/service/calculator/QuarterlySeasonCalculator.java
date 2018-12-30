package com.texastoc.service.calculator;

import com.texastoc.model.game.Game;
import com.texastoc.model.game.GamePlayer;
import com.texastoc.model.season.QuarterlySeason;
import com.texastoc.model.season.Season;
import com.texastoc.repository.GamePlayerRepository;
import com.texastoc.repository.QuarterlySeasonRepository;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class QuarterlySeasonCalculator {

    private final QuarterlySeasonRepository qSeasonRepository;
    private final GamePlayerRepository gamePlayerRepository;

    public QuarterlySeasonCalculator(QuarterlySeasonRepository qSeasonRepository, GamePlayerRepository gamePlayerRepository) {
        this.qSeasonRepository = qSeasonRepository;
        this.gamePlayerRepository = gamePlayerRepository;
    }

    public QuarterlySeason calculate(int id) {
        QuarterlySeason qSeason = qSeasonRepository.getById(id);

        List<GamePlayer> players = gamePlayerRepository.selectQuarterlyTocPlayersByQuarterlySeasonId(id);
        qSeason.setPlayers(Collections.emptyList());
        qSeason.setPayouts(Collections.emptyList());

        return qSeason;
    }

}
