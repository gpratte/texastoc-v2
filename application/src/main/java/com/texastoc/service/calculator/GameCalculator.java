package com.texastoc.service.calculator;

import com.texastoc.model.config.TocConfig;
import com.texastoc.model.game.Game;
import com.texastoc.model.game.GamePlayer;
import com.texastoc.repository.ConfigRepository;
import com.texastoc.repository.GamePlayerRepository;
import com.texastoc.repository.GameRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class GameCalculator {

    private final GameRepository gameRepository;
    private final GamePlayerRepository gamePlayerRepository;
    private final ConfigRepository configRepository;
    private TocConfig tocConfig;

    public GameCalculator(GameRepository gameRepository, GamePlayerRepository gamePlayerRepository, ConfigRepository configRepository) {
        this.gameRepository = gameRepository;
        this.gamePlayerRepository = gamePlayerRepository;
        this.configRepository = configRepository;
    }

    public Game calculate(Game game, List<GamePlayer> gamePlayers) {

        int kittyCollected = 0;
        int numPlayers = 0;
        int buyInCollected = 0;
        int rebuyAddOnCollected = 0;
        int annualTocCollected = 0;
        int quarterlyTocCollected = 0;
        int rebuyAddOnTocCollected = 0;

        for (GamePlayer gamePlayer : gamePlayers) {
            ++numPlayers;
            buyInCollected += gamePlayer.getBuyInCollected() == null ? 0 : gamePlayer.getBuyInCollected();
            rebuyAddOnCollected += gamePlayer.getRebuyAddOnCollected() == null ? 0 : gamePlayer.getRebuyAddOnCollected();
            annualTocCollected += gamePlayer.getAnnualTocCollected() == null ? 0 : gamePlayer.getAnnualTocCollected();
            quarterlyTocCollected += gamePlayer.getQuarterlyTocCollected() == null ? 0 : gamePlayer.getQuarterlyTocCollected();

            boolean isAnnualToc = gamePlayer.getAnnualTocCollected() != null && gamePlayer.getAnnualTocCollected() > 0;
            boolean isRebuyAddOn = gamePlayer.getRebuyAddOnCollected() != null && gamePlayer.getRebuyAddOnCollected() > 0;
            if (isAnnualToc && isRebuyAddOn) {
                rebuyAddOnTocCollected += getTocConfig().getRegularRebuyTocDebit();
            }
        }

        if (buyInCollected > 0) {
            kittyCollected = getTocConfig().getKittyDebit();
        }

        game.setKittyCollected(kittyCollected);
        game.setNumPlayers(numPlayers);
        game.setBuyInCollected(buyInCollected);
        game.setRebuyAddOnCollected(rebuyAddOnCollected);
        game.setAnnualTocCollected(annualTocCollected);
        game.setQuarterlyTocCollected(quarterlyTocCollected);
        game.setLastCalculated(LocalDateTime.now());
        game.setRebuyAddOnTocCollected(rebuyAddOnTocCollected);

        int totalCollected = buyInCollected + rebuyAddOnCollected + annualTocCollected + quarterlyTocCollected;
        game.setTotalCollected(totalCollected);

        int totalTocCollected = annualTocCollected + quarterlyTocCollected + rebuyAddOnTocCollected;
        game.setTotalTocCollected(totalTocCollected);

        // prizePot = total collected minus total toc minus kitty =  59</li>
        game.setPrizePot(totalCollected - totalTocCollected - kittyCollected);

        gameRepository.update(game);

        return game;
    }

    // Cache it
    private TocConfig getTocConfig() {
        if (tocConfig == null) {
            tocConfig = configRepository.get();
        }
        return tocConfig;
    }
}
