package com.texastoc.service;

import com.texastoc.model.game.Game;
import com.texastoc.model.season.QuarterlySeason;
import com.texastoc.model.season.Season;
import com.texastoc.model.user.Player;
import com.texastoc.repository.GameRepository;
import com.texastoc.repository.PlayerRepository;
import com.texastoc.repository.QuarterlySeasonRepository;
import com.texastoc.repository.SeasonRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class GameService {

    private final GameRepository gameRepository;
    private final PlayerRepository playerRepository;
    private final SeasonRepository seasonRepository;
    private final QuarterlySeasonRepository qSeasonRepository;

    public GameService(GameRepository gameRepository, PlayerRepository playerRepository, SeasonRepository seasonRepository, QuarterlySeasonRepository qSeasonRepository) {
        this.gameRepository = gameRepository;
        this.playerRepository = playerRepository;
        this.seasonRepository = seasonRepository;
        this.qSeasonRepository = qSeasonRepository;
    }

    public Game createGame(Game game) {
        Game gameToCreate = new Game();

        // TODO check that date is allowed - not before an existing game and not beyond the season.
        gameToCreate.setDate(game.getDate());

        // TODO exception if null
        QuarterlySeason currentQSeason = qSeasonRepository.getCurrent();
        gameToCreate.setSeasonId(currentQSeason.getSeasonId());
        gameToCreate.setQSeasonId(currentQSeason.getId());
        gameToCreate.setQuarter(currentQSeason.getQuarter());

        Player player = playerRepository.get(game.getHostId());
        gameToCreate.setHostId(game.getHostId());
        gameToCreate.setHostName(player.getName());

        gameToCreate.setDoubleBuyIn(game.getDoubleBuyIn());
        gameToCreate.setTransportRequired(game.getTransportRequired());

        Season currentSeason = seasonRepository.getCurrent();
        gameToCreate.setKittyCost(currentSeason.getKittyPerGame());

        gameToCreate.setAnnualTocCost(currentSeason.getTocPerGame());
        gameToCreate.setQuarterlyTocCost(currentSeason.getQuarterlyTocPerGame());

        gameToCreate.setNumPlayers(0);
        gameToCreate.setKittyCollected(0);
        gameToCreate.setBuyInCollected(0);
        gameToCreate.setRebuyAddOnCollected(0);
        gameToCreate.setAnnualTocCollected(0);
        gameToCreate.setQuarterlyTocCollected(0);

        if (isGameFirstOfMonth(gameToCreate.getDate())) {
            gameToCreate.setBuyInCost(currentSeason.getDoubleBuyInCost());
            gameToCreate.setRebuyAddOnCost(currentSeason.getDoubleRebuyAddOnCost());
            gameToCreate.setRebuyAddOnTocDebit(currentSeason.getDoubleRebuyAddOnTocDebit());
        } else {
            gameToCreate.setBuyInCost(currentSeason.getBuyInCost());
            gameToCreate.setRebuyAddOnCost(currentSeason.getRebuyAddOnCost());
            gameToCreate.setRebuyAddOnTocDebit(currentSeason.getRebuyAddOnTocDebit());
        }

        gameToCreate.setFinalized(false);

        int id = gameRepository.save(gameToCreate);
        gameToCreate.setId(id);

        return gameToCreate;
    }

    private boolean isGameFirstOfMonth(LocalDate date) {
        // TODO
        return true;
    }
}
