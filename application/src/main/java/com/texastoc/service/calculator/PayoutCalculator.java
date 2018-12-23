package com.texastoc.service.calculator;

import com.texastoc.model.common.Payout;
import com.texastoc.model.game.Game;
import com.texastoc.model.game.GamePayout;
import com.texastoc.model.game.GamePlayer;
import com.texastoc.repository.PayoutRepository;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

@Component
public class PayoutCalculator {

    private final PayoutRepository payoutRepository;

    public PayoutCalculator(PayoutRepository payoutRepository) {
        this.payoutRepository = payoutRepository;
    }

    public List<GamePayout> calculate(Game game) {

        if (game.getPrizePot() <= 0) {
            return Collections.EMPTY_LIST;
        }

        // round to multiple of 5 (e.g. 12 rounds to 10 but 13 rounds to 15)
        int numPlayers = (int)Math.round((double)game.getNumPlayers()/5) * 5;

        int numberPaid = numPlayers / 5;
        numberPaid += game.getPayoutDelta() == null ? 0 : game.getPayoutDelta();

        // Always pay at least 1 player
        if (numberPaid < 1) {
            numberPaid = 1;
        }
        return calculatePayout(numberPaid, game);
    }

    private List<GamePayout> calculatePayout(int numToPay, Game game) {

        List<GamePayout> gamePayouts = new ArrayList<>(numToPay);

        // If only one player then he gets it all
        if (numToPay == 1) {
            gamePayouts.add(GamePayout.builder()
                .gameId(game.getId())
                .place(1)
                .amount(game.getPrizePot())
                .build());
            return gamePayouts;
        }

        List<Payout> payouts = payoutRepository.get(numToPay);
        int prizePot = game.getPrizePot();
        int totalPayout = 0;
        for (Payout payout : payouts) {
            GamePayout gp = new GamePayout();
            gp.setGameId(game.getId());
            gp.setPlace(payout.getPlace());
            double percent = payout.getPercent();
            int amount = (int)Math.round(percent * prizePot);
            gp.setAmount(amount);
            totalPayout += amount;
            gamePayouts.add(gp);
        }
//
//        if (totalPayout > prizePot) {
//            int extra = totalPayout - prizePot;
//            while (extra > 0) {
//                for (int i = gamePayouts.size() - 1; i >= 0; --i) {
//                    GamePayout gp = gamePayouts.get(i);
//                    gp.setAmount(gp.getAmount() - 1);
//                    if (--extra == 0) {
//                        break;
//                    }
//                }
//            }
//        } else if (totalPayout < prizePot) {
//            int extra = prizePot - totalPayout;
//            while (extra > 0) {
//                for (GamePayout gp : gamePayouts) {
//                    gp.setAmount(gp.getAmount() + 1);
//                    if (--extra == 0) {
//                        break;
//                    }
//                }
//            }
//        }
//
//        // See if there is a chop
//        List<Integer> chips = null;
//        List<Integer> amounts = null;
//        for (GamePlayer player : game.getPlayers()) {
//            if (player.getChop() != null) {
//                if (chips == null) {
//                    chips = new ArrayList<Integer>();
//                    chips.add(player.getChop());
//                    amounts = new ArrayList<Integer>();
//                    for (GamePayout gamePayout : gamePayouts) {
//                        if (gamePayout.getPlace() == player.getFinish()) {
//                            amounts.add(gamePayout.getAmount());
//                            break;
//                        }
//                    }
//                } else {
//                    boolean inserted = false;
//                    for (int i = 0; i < chips.size(); ++i) {
//                        if (player.getChop().intValue() >= chips.get(i).intValue()) {
//                            chips.add(i, player.getChop());
//                            for (GamePayout gamePayout : gamePayouts) {
//                                if (gamePayout.getPlace() == player.getFinish()) {
//                                    amounts.add(i, gamePayout.getAmount());
//                                    inserted = true;
//                                    break;
//                                }
//                            }
//                        }
//                    }
//                    if (!inserted) {
//                        chips.add(player.getChop());
//                        for (GamePayout gamePayout : gamePayouts) {
//                            if (gamePayout != null && player != null && gamePayout.getPlace() == player.getFinish()) {
//                                amounts.add(gamePayout.getAmount());
//                                break;
//                            }
//                        }
//                    }
//                }
//            }
//        }
//
//        if (chips != null) {
//            // If chips are more than the number of payouts then there
//            // is a problem because even though the top x chopped the
//            // points less than that gets paid.
//            int numChopThatGetPaid = Math.min(chips.size(), payouts.size());
//            List<Chop> chops = chopCalculator.calculate(
//                chips.subList(0, numChopThatGetPaid),
//                amounts.subList(0, numChopThatGetPaid));
//            if (chops != null && chops.size() > 1) {
//                for (Chop chop : chops) {
//                    outer: for (GamePlayer player : game.getPlayers()) {
//                        if (player.getChop() != null) {
//                            for (GamePayout gamePayout : gamePayouts) {
//                                if (gamePayout.getAmount() == chop.getOrgAmount()) {
//                                    gamePayout.setChopAmount(chop.getChopAmount());
//                                    gamePayout.setChopPercent(chop.getPercent());
//                                    break outer;
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//        }
//
//        List<GamePayout> currentPayouts = gamePayoutDao.selectByGameId(game.getId());
//
//        // Add or update existing
//        for (GamePayout gp : gamePayouts) {
//            boolean found = false;
//            for (GamePayout currentPayout : currentPayouts) {
//                if (gp.getPlace() == currentPayout.getPlace()) {
//                    // update
//                    gamePayoutDao.update(gp);
//                    found = true;
//                    break;
//                }
//            }
//
//            if (!found) {
//                // add
//                gamePayoutDao.insert(gp);
//            }
//        }
//
//        // Remove
//        for (GamePayout currentPayout : currentPayouts) {
//            boolean found = false;
//            for (GamePayout gp : gamePayouts) {
//                if (gp.getPlace() == currentPayout.getPlace()) {
//                    found = true;
//                    break;
//                }
//            }
//
//            if (!found) {
//                // remove
//                gamePayoutDao.delete(currentPayout.getGameId(), currentPayout.getPlace());
//            }
//        }
        return gamePayouts;
    }

    private void calculateChopPayout(List<GamePayout> gamePayouts, Game game) {
//        int smallestPayout = 0;
//        int numPlayersThatChopped = 0;
//        int combinedPayout = 0;
//        int combinedChips = 0;
//
//        for (GamePlayer gamePlayer : game.getPlayers()) {
//            if (gamePlayer.getFinish() != null && gamePlayer.getChop() != null) {
//                GamePayout gamePayout = getPayoutForFinish(gamePlayer.getFinish().intValue(), gamePayouts);
//                if (gamePayout == null) {
//                    return;
//                }
//                int payout = gamePayout.getAmount();
//                if (numPlayersThatChopped == 0) {
//                    smallestPayout = payout;
//                } else {
//                    smallestPayout = Math.min(smallestPayout, payout);
//                }
//
//                ++numPlayersThatChopped;
//                combinedPayout += payout;
//                combinedChips += gamePlayer.getChop();
//            }
//        }
//
//        // Calculate percentage
//        for (GamePlayer gamePlayer : game.getPlayers()) {
//            if (gamePlayer.getFinish() != null && gamePlayer.getChop() != null) {
//                GamePayout gamePayout = getPayoutForFinish(gamePlayer.getFinish().intValue(), gamePayouts);
//                if (gamePayout == null) {
//                    return;
//                }
//                float percent = gamePlayer.getChop() / (float) combinedChips;
//                gamePayout.setChopPercent(percent);
//            }
//        }
//
//        // For all that chopped give them the smallest and then their
//        // percent of the remaining
//        int amountToChop = combinedPayout - (smallestPayout * numPlayersThatChopped);
//        for (GamePayout gamePayout : gamePayouts) {
//            if (gamePayout.getChopPercent() != null) {
//                int choppedAmount = (int)(amountToChop * gamePayout.getChopPercent());
//                gamePayout.setChopAmount(choppedAmount + smallestPayout);
//            }
//        }
//
//        // Now make sure all the money is account for (because when using
//        // floats for percentages the int are rounded)
//        int recalculatedCombinedPayouts = 0;
//        for (GamePayout gamePayout : gamePayouts) {
//            if (gamePayout.getChopPercent() != null) {
//                recalculatedCombinedPayouts += gamePayout.getChopAmount();
//            }
//        }
//
//        recalculatePayouts(combinedPayout - recalculatedCombinedPayouts, numPlayersThatChopped, gamePayouts);
    }

    private GamePayout getPayoutForFinish(int finish, List<GamePayout> gamePayouts) {
//        for (GamePayout gamePayout : gamePayouts) {
//            if (gamePayout.getPlace() == finish) {
//                return gamePayout;
//            }
//        }

        return null;
    }

    private void recalculatePayouts(int delta, int numThatChopped, List<GamePayout> gamePayouts) {
//        if (delta == 0) {
//            return;
//        } else if (delta > 0) {
//            for (int i = 1; i <= delta; ++i) {
//                changeChop(i, true, numThatChopped, gamePayouts);
//            }
//        } else {
//            delta *= -1;
//            for (int i = 1; i <= delta; --i) {
//                changeChop(i, true, numThatChopped, gamePayouts);
//            }
//        }
    }

    private void changeChop(int place, boolean increase, int numThatChopped, List<GamePayout> gamePayouts) {
//        int placeToChange = place % numThatChopped;
//        if (placeToChange == 0) {
//            placeToChange = numThatChopped;
//        }
//
//        GamePayout gamePayout = getPayoutForFinish(placeToChange, gamePayouts);
//        if (increase) {
//            gamePayout.setChopAmount(gamePayout.getChopAmount() + 1);
//        } else {
//            gamePayout.setChopAmount(gamePayout.getChopAmount() - 1);
//        }
    }
}
