package com.texastoc.service.calculator;

import com.texastoc.model.game.Game;
import com.texastoc.model.game.GamePlayer;
import com.texastoc.model.season.Season;
import com.texastoc.model.season.SeasonPayout;
import com.texastoc.model.season.SeasonPlayer;
import com.texastoc.repository.GamePlayerRepository;
import com.texastoc.repository.GameRepository;
import com.texastoc.repository.SeasonPlayerRepository;
import com.texastoc.repository.SeasonRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Component
public class SeasonCalculator {

    private final GameRepository gameRepository;
    private final SeasonRepository seasonRepository;
    private final GamePlayerRepository gamePlayerRepository;
    private final SeasonPlayerRepository seasonPlayerRepository;

    public SeasonCalculator(GameRepository gameRepository, SeasonRepository seasonRepository, SeasonPlayerRepository seasonPlayerRepository, GamePlayerRepository gamePlayerRepository) {
        this.gameRepository = gameRepository;
        this.seasonRepository = seasonRepository;
        this.seasonPlayerRepository = seasonPlayerRepository;
        this.gamePlayerRepository = gamePlayerRepository;
    }

    public Season calculate(int id) {

        Season season = seasonRepository.get(id);
        List<Game> games = gameRepository.getBySeasonId(id);

        season.setNumGamesPlayed(games.size());

        int buyInCollected = 0;
        int rebuyAddOnCollected = 0;
        int annualTocCollected = 0;
        int totalCollected = 0;

        int annualTocFromRebuyAddOnCalculated = 0;
        int rebuyAddOnLessAnnualTocCalculated = 0;
        int totalCombinedAnnualTocCalculated = 0;
        int kittyCalculated = 0;
        int prizePotCalculated = 0;

        for (Game game : games) {
            buyInCollected += game.getBuyInCollected();
            rebuyAddOnCollected += game.getRebuyAddOnCollected();
            annualTocCollected += game.getAnnualTocCollected();
            totalCollected += game.getTotalCollected();

            annualTocFromRebuyAddOnCalculated += game.getAnnualTocFromRebuyAddOnCalculated();
            rebuyAddOnLessAnnualTocCalculated += game.getRebuyAddOnLessAnnualTocCalculated();
            totalCombinedAnnualTocCalculated += game.getTotalCombinedTocCalculated();
            kittyCalculated += game.getKittyCalculated();
            prizePotCalculated += game.getPrizePotCalculated();
        }

        season.setBuyInCollected(buyInCollected);
        season.setRebuyAddOnCollected(rebuyAddOnCollected);
        season.setAnnualTocCollected(annualTocCollected);
        season.setTotalCollected(totalCollected);

        season.setAnnualTocFromRebuyAddOnCalculated(annualTocFromRebuyAddOnCalculated);
        season.setRebuyAddOnLessAnnualTocCalculated(rebuyAddOnLessAnnualTocCalculated);
        season.setTotalCombinedAnnualTocCalculated(totalCombinedAnnualTocCalculated);
        season.setKittyCalculated(kittyCalculated);
        season.setPrizePotCalculated(prizePotCalculated);

        season.setLastCalculated(LocalDateTime.now());

        // Season players
        List<SeasonPlayer> seasonPlayers = calculatePlayers(id);
        season.setPlayers(seasonPlayers);

        // Payouts
        List<SeasonPayout> payouts = new ArrayList<>(10);
        season.setPayouts(payouts);

        return season;
    }

    private List<SeasonPlayer> calculatePlayers(int id) {

        Map<Integer, SeasonPlayer> seasonPlayerMap = new HashMap<>();

        List<GamePlayer> gamePlayers = gamePlayerRepository.selectAnnualTocPlayersBySeasonId(id);
        for (GamePlayer gamePlayer : gamePlayers) {
            SeasonPlayer seasonPlayer = seasonPlayerMap.get(gamePlayer.getId());
            if (seasonPlayer == null) {

                seasonPlayer = SeasonPlayer.builder()
                    .playerId(gamePlayer.getPlayerId())
                    .seasonId(id)
                    .name(gamePlayer.getName())
                    .build();

                seasonPlayerMap.put(gamePlayer.getId(), seasonPlayer);
            }

            if (gamePlayer.getPoints() != null && gamePlayer.getPoints() > 0) {
                seasonPlayer.setPoints(seasonPlayer.getPoints() + gamePlayer.getPoints());
            }

            seasonPlayer.setEntries(seasonPlayer.getEntries() + 1);
        }

        return new ArrayList<>(seasonPlayerMap.values());
    }

    //    @Autowired
//    GameDao gameDao;
//    @Autowired
//    SeasonDao seasonDao;
//    @Autowired
//    SupplyDao supplyDao;
//    @Autowired
//    PlayerDao playerDao;
//    @Autowired
//    SeasonPlayerDao seasonPlayerDao;
//
//    @Override
//    public void calculate(int id) throws Exception {
//        Season season = seasonDao.selectById(id);
//
//        if (season.isUseHistoricalData()) {
//            throw new CannotCalculateException(
//                "Season is using historical data");
//        }
//
//        if (season.isFinalized()) {
//            throw new CannotCalculateException("Season is finalized");
//        }
//
//        ArrayList<SeasonPlayer> seasonPlayers = calculate(season, null, null);
//        seasonDao.update(season);
//
//        List<SeasonPlayer> currentSeasonPlayers = seasonPlayerDao
//            .selectBySeasonId(season.getId());
//
//        // Add or update existing
//        for (SeasonPlayer player : seasonPlayers) {
//            boolean found = false;
//            for (SeasonPlayer currentPlayer : currentSeasonPlayers) {
//                if (player.getPlayerId() == currentPlayer.getPlayerId()) {
//                    // update
//                    currentPlayer.setNumEntries(player.getNumEntries());
//                    currentPlayer.setPlace(player.getPlace());
//                    currentPlayer.setPoints(player.getPoints());
//                    seasonPlayerDao.update(currentPlayer);
//                    found = true;
//                    break;
//                }
//            }
//
//            if (!found) {
//                // add
//                seasonPlayerDao.insert(player);
//            }
//        }
//
//        // Remove
//        for (SeasonPlayer currentPlayer : currentSeasonPlayers) {
//            boolean found = false;
//            for (SeasonPlayer player : seasonPlayers) {
//                if (player.getPlayerId() == currentPlayer.getPlayerId()) {
//                    found = true;
//                    break;
//                }
//            }
//
//            if (!found) {
//                // remove
//                seasonPlayerDao.delete(currentPlayer.getId());
//            }
//        }
//
//        // Persist the payouts
//        List<SeasonPayout> payouts = calculatePayouts(season);
//        seasonDao.deleteTempPayoutsBySeasonId(season.getId());
//        for (SeasonPayout payout : payouts) {
//            if (payout.isTemp()) {
//                seasonDao.insertPayout(payout);
//            }
//        }
//    }
//
//    @Override
//    public Season calcluateUpToGame(int gameId) {
//        Game upToGame = gameDao.selectById(gameId);
//        Season season = seasonDao.selectById(upToGame.getSeasonId());
//        Season upToSeason = new Season();
//        upToSeason.setEndDate(season.getEndDate());
//        upToSeason.setFinalized(season.isFinalized());
//        upToSeason.setId(season.getId());
//        upToSeason.setLastCalculated(season.getLastCalculated());
//        upToSeason.setNote(season.getNote());
//        upToSeason.setStartDate(season.getStartDate());
//        upToSeason.setUseHistoricalData(season.isUseHistoricalData());
//        upToSeason.setFinalTableImage(season.getFinalTableImage());
//        upToSeason.setFinalTableThumb(season.getFinalTableThumb());
//        upToSeason.setQuarterlyTocAmount(season.getQuarterlyTocAmount());
//        upToSeason.setQuarterlyTocPayouts(season.getQuarterlyTocPayouts());
//
//        ArrayList<SeasonPlayer> seasonPlayers = calculate(season, upToSeason,
//            upToGame);
//
//        for (SeasonPlayer seasonPlayer : seasonPlayers) {
//            seasonPlayer.setPlayer(playerDao.selectById(seasonPlayer
//                .getPlayerId()));
//            // Fill in other season player info if available
//            for (SeasonPlayer existingSeasonPlayer : season.getSeasonPlayers()) {
//                if (seasonPlayer.getPlayerId() == existingSeasonPlayer
//                    .getPlayerId()) {
//                    seasonPlayer.setForfeit(existingSeasonPlayer.isForfeit());
//                    seasonPlayer.setWsop(existingSeasonPlayer.isWsop());
//                    seasonPlayer.setTie(existingSeasonPlayer.getTie());
//                    break;
//                }
//            }
//        }
//
//        // Reorder if there is a tie
//        boolean isATie = false;
//        SeasonPlayer previousPlayer = null;
//        for (SeasonPlayer player : seasonPlayers) {
//            if (previousPlayer != null) {
//                if (player.getPoints() == previousPlayer.getPoints()) {
//                    isATie = true;
//                    break;
//                }
//            }
//            previousPlayer = player;
//        }
//
//        if (isATie) {
//            boolean sorted = false;
//            do {
//                sorted = bubbleSort(seasonPlayers);
//            } while (sorted);
//        }
//
//        upToSeason.setSeasonPlayers(seasonPlayers);
//
//        List<SeasonPayout> payouts = calculatePayouts(upToSeason);
//        upToSeason.setPayouts(payouts);
//
//        return upToSeason;
//    }
//
//    private ArrayList<SeasonPlayer> calculate(Season season, Season upToSeason,
//                                              Game upToGame) {
//
//        int totalBuyIn = 0;
//        int totalReBuy = 0;
//        int totalAnnualToc = 0;
//        int totalAnnualTocSupplies = 0;
//
//        LocalDate maxGameDate = null;
//
//        HashMap<Integer, SeasonPlayer> playerMap = new HashMap<Integer, SeasonPlayer>();
//        for (Game game : season.getGames()) {
//
//            if (!game.isFinalized()) {
//                continue;
//            }
//
//            if (upToGame != null
//                && game.getGameDate().isAfter(upToGame.getGameDate())) {
//                continue;
//            }
//
//            if (maxGameDate == null || maxGameDate.isBefore(game.getGameDate())) {
//                maxGameDate = game.getGameDate();
//            }
//
//            totalBuyIn += game.getTotalBuyIn();
//            totalReBuy += game.getTotalReBuy();
//            totalAnnualToc += game.getTotalAnnualToc();
//            totalAnnualTocSupplies += game.getTotalAnnualTocSupplies();
//
//            for (GamePlayer gp : game.getPlayers()) {
//                if (!gp.isAnnualTocPlayer()) {
//                    continue;
//                }
//
//                SeasonPlayer seasonPlayer = playerMap.get(gp.getPlayerId());
//                if (seasonPlayer == null) {
//                    seasonPlayer = new SeasonPlayer();
//                    seasonPlayer.setSeasonId(season.getId());
//                    seasonPlayer.setPlayerId(gp.getPlayerId());
//                    playerMap.put(gp.getPlayerId(), seasonPlayer);
//                }
//
//                seasonPlayer.setNumEntries(seasonPlayer.getNumEntries() + 1);
//
//                if (gp.getPoints() != null) {
//                    int points = seasonPlayer.getPoints() + gp.getPoints();
//                    seasonPlayer.setPoints(points);
//                }
//            }
//
//        }
//
//        // Add in whatever supplies there are for the season that are
//        // not tied to a game
//        int seasonSupplyTotal = 0;
//        List<Supply> supplies = supplyDao.selectSuppliesForSeason(season
//            .getId());
//        for (Supply supply : supplies) {
//            if (supply.getGameId() != null) {
//                continue;
//            }
//            if (supply.getCreateDate().isAfter(maxGameDate)) {
//                continue;
//            }
//            if (supply.getAnnualTocAmount() == null) {
//                continue;
//            }
//            seasonSupplyTotal += supply.getAnnualTocAmount();
//        }
//
//        if (upToSeason != null) {
//            upToSeason.setTotalAnnualToc(totalAnnualToc);
//            upToSeason.setTotalAnnualTocSupplies(totalAnnualTocSupplies
//                + seasonSupplyTotal);
//            upToSeason.setTotalBuyIn(totalBuyIn);
//            upToSeason.setTotalReBuy(totalReBuy);
//        } else {
//            season.setTotalAnnualToc(totalAnnualToc);
//            season.setTotalAnnualTocSupplies(totalAnnualTocSupplies
//                + seasonSupplyTotal);
//            season.setTotalBuyIn(totalBuyIn);
//            season.setTotalReBuy(totalReBuy);
//        }
//
//        ArrayList<SeasonPlayer> seasonPlayers = new ArrayList<SeasonPlayer>(
//            playerMap.values());
//
//        Collections.sort(seasonPlayers);
//
//        int count = 1;
//        for (SeasonPlayer player : seasonPlayers) {
//            if (player.getPoints() > 0) {
//                player.setPlace(count++);
//            }
//        }
//
//        return seasonPlayers;
//    }
//
//    private List<SeasonPayout> calculatePayouts(Season season) {
//
////        List<SeasonPayout> currentPayouts = seasonDao
////                .selectPayoutsBySeasonId(season.getId());
//
//        List<SeasonPayout> currentPayouts = getTocSeasonPayouts(season);
//
//        List<SeasonPayout> payouts = new ArrayList<SeasonPayout>();
//
//        int annualTocAmount = season.getTotalAnnualToc();
//
//        SeasonPayout asteriskPayout = null;
//        for (SeasonPayout payout : currentPayouts) {
//            if ("*".equals(payout.getPlace())) {
//                asteriskPayout = payout;
//            } else {
//                if (!payout.isTemp()) {
//                    if (annualTocAmount > payout.getAmount()) {
//                        payouts.add(payout);
//                        if (annualTocAmount > 0) {
//                            annualTocAmount -= payout.getAmount();
//                        }
//                    } else {
//                        payout.setAmount(annualTocAmount);
//                        payouts.add(payout);
//                        annualTocAmount = 0;
//                    }
//                }
//            }
//        }
//
//        // See if there is any money left and if there is an asterisk
//        if (asteriskPayout != null) {
//            int count = 0;
//            while (annualTocAmount > 0) {
//                SeasonPayout payout = new SeasonPayout();
//                payouts.add(payout);
//                payout.setTemp(true);
//                payout.setSeasonId(season.getId());
//                payout.setPlace("Final table " + (++count));
//                if (annualTocAmount >= asteriskPayout.getAmount()) {
//                    payout.setAmount(asteriskPayout.getAmount());
//                    annualTocAmount -= asteriskPayout.getAmount();
//                } else {
//                    payout.setAmount(annualTocAmount);
//                    annualTocAmount = 0;
//                }
//            }
//        }
//
//        return payouts;
//    }
//
//    // Hardcoded
//    List<SeasonPayout> getTocSeasonPayouts(Season season) {
//        List<SeasonPayout> seasonPayouts = new ArrayList<SeasonPayout>();
//
//        if (season.getTotalAnnualToc() < 10000) {
//            SeasonPayout seasonPayout = new SeasonPayout();
//            seasonPayout.setPlace("1");
//            seasonPayout.setAmount(2000);
//            seasonPayout.setSeasonId(season.getId());
//            seasonPayouts.add(seasonPayout);
//        } else if (season.getTotalAnnualToc() < 10499) {
//            SeasonPayout seasonPayout = new SeasonPayout();
//            seasonPayout.setPlace("1");
//            seasonPayout.setAmount(2000);
//            seasonPayout.setSeasonId(season.getId());
//            seasonPayouts.add(seasonPayout);
//
//            seasonPayout = new SeasonPayout();
//            seasonPayout.setPlace("2");
//            seasonPayout.setAmount(2000);
//            seasonPayout.setSeasonId(season.getId());
//            seasonPayouts.add(seasonPayout);
//        } else {
//            SeasonPayout seasonPayout = new SeasonPayout();
//            seasonPayout.setPlace("1");
//            seasonPayout.setAmount(2500);
//            seasonPayout.setSeasonId(season.getId());
//            seasonPayouts.add(seasonPayout);
//
//            seasonPayout = new SeasonPayout();
//            seasonPayout.setPlace("2");
//            seasonPayout.setAmount(2000);
//            seasonPayout.setSeasonId(season.getId());
//            seasonPayouts.add(seasonPayout);
//        }
//
//        SeasonPayout seasonPayout = new SeasonPayout();
//        seasonPayout.setPlace("*");
//        seasonPayout.setAmount(2000);
//        seasonPayout.setSeasonId(season.getId());
//        seasonPayouts.add(seasonPayout);
//
//        return seasonPayouts;
//    }
//
//    private boolean bubbleSort(List<SeasonPlayer> players) {
//        boolean sorted = false;
//
//        int indexSwap = 0;
//        for (int i = 0; i < players.size(); ++i) {
//            if (i == 0) {
//                continue;
//            }
//            SeasonPlayer previousPlayer = players.get(i - 1);
//            SeasonPlayer player = players.get(i);
//            if (previousPlayer.getPoints() == player.getPoints()) {
//                if (previousPlayer.getTie() != null &&
//                    player.getTie() != null) {
//                    if (previousPlayer.getTie() > player.getTie()) {
//                        indexSwap = i -1;
//                        sorted = true;
//                        break;
//                    }
//                }
//            }
//        }
//
//        if (sorted) {
//            SeasonPlayer savePlayer = players.get(indexSwap);
//            players.set(indexSwap, players.get(indexSwap + 1));
//            players.set(indexSwap + 1, savePlayer);
//        }
//
//        return sorted;
//    }

}
