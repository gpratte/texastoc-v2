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
        gameToCreate.setTransportSupplies(game.getTransportSupplies());
        gameToCreate.setDoubleBuyIn(game.getDoubleBuyIn());

        Player player = playerRepository.get(game.getHostId());
        gameToCreate.setHostId(game.getHostId());
        gameToCreate.setHostName(player.getName());

        // TODO exception if null
        QuarterlySeason currentQSeason = qSeasonRepository.getCurrent();

        gameToCreate.setSeasonId(currentQSeason.getSeasonId());
        gameToCreate.setQSeasonId(currentQSeason.getId());
        gameToCreate.setQuarter(currentQSeason.getQuarter());

        gameToCreate.setNumPlayers(0);
        gameToCreate.setBuyIn(0);
        gameToCreate.setRebuyAddOn(0);

        Season currentSeason = seasonRepository.getCurrent();
        gameToCreate.setKitty(currentSeason.getKittyPerGame());
        gameToCreate.setAnnualTocAmount(currentSeason.getTocPerGame());
        gameToCreate.setQuarterlyTocAmount(currentSeason.getQuarterlyTocPerGame());

        int id = gameRepository.save(gameToCreate);
        gameToCreate.setId(id);

        return gameToCreate;
    }
}
