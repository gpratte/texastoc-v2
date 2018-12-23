package com.texastoc.service.calculator;

import com.texastoc.TestConstants;
import com.texastoc.model.common.Payout;
import com.texastoc.model.game.Game;
import com.texastoc.model.game.GamePayout;
import com.texastoc.repository.PayoutRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@RunWith(SpringRunner.class)
public class PayoutCalculatorTest implements TestConstants {

    private PayoutCalculator payoutCalculator;
    private Random random = new Random(System.currentTimeMillis());

    @MockBean
    private PayoutRepository payoutRepository;

    @Before
    public void before() {
        payoutCalculator = new PayoutCalculator(payoutRepository);
    }

    @Test
    public void testNoPlayersNoPayouts() {

        Game game = Game.builder()
            .id(1)
            .numPlayers(0)
            .prizePot(0)
            .build();

        List<GamePayout> gamePayouts = payoutCalculator.calculate(game);

        Assert.assertNotNull("list of game payouts should not be null", gamePayouts);
        Assert.assertEquals("list of game payouts should be size 0", 0, gamePayouts.size());
    }

    @Test
    public void test1PlayersNoPayouts() {

        Game game = Game.builder()
            .id(1)
            .numPlayers(1)
            .prizePot(0)
            .build();

        List<GamePayout> gamePayouts = payoutCalculator.calculate(game);

        Assert.assertNotNull("list of game payouts should not be null", gamePayouts);
        Assert.assertEquals("list of game payouts should be size 0", 0, gamePayouts.size());
    }

    @Test
    public void test1Players1Payout() {

        Game game = Game.builder()
            .id(1)
            .numPlayers(1)
            .prizePot(GAME_BUY_IN)
            .build();

        List<GamePayout> gamePayouts = payoutCalculator.calculate(game);

        Assert.assertNotNull("list of game payouts should not be null", gamePayouts);
        Assert.assertEquals("list of game payouts should be size 1", 1, gamePayouts.size());

        GamePayout gamePayout = gamePayouts.get(0);
        Assert.assertEquals("payout should be place 1", 1, gamePayout.getPlace());
        Assert.assertEquals("payout amount should be " + GAME_BUY_IN, GAME_BUY_IN, gamePayout.getAmount());
        Assert.assertEquals("payout chop should be 0", 0, gamePayout.getChopAmount());
        Assert.assertEquals(0.0, gamePayout.getChopPercent(), 0.0);
    }

    @Test
    public void testUpTo7Players1Payout() {

        // Create between 1 and 7 players
        int numPlayers = 0;
        while (numPlayers == 0) {
            numPlayers = random.nextInt(8);
        }

        Game game = Game.builder()
            .id(1)
            .numPlayers(numPlayers)
            .prizePot(GAME_BUY_IN * numPlayers)
            .build();

        List<GamePayout> gamePayouts = payoutCalculator.calculate(game);

        Assert.assertNotNull("list of game payouts should not be null", gamePayouts);
        Assert.assertEquals("list of game payouts should be size 1", 1, gamePayouts.size());

        GamePayout gamePayout = gamePayouts.get(0);
        Assert.assertEquals("payout should be place 1", 1, gamePayout.getPlace());
        Assert.assertEquals("payout amount should be " + (GAME_BUY_IN * numPlayers), GAME_BUY_IN * numPlayers, gamePayout.getAmount());
        Assert.assertEquals("payout chop should be 0", 0, gamePayout.getChopAmount());
        Assert.assertEquals(0.0, gamePayout.getChopPercent(), 0.0);
    }

    @Test
    public void test8To12Players2Payouts() {

        // Create between 8 and 12 players
        int numPlayers = 0;
        while (numPlayers == 0) {
            numPlayers = random.nextInt(5);
        }
        numPlayers += 7;

        int prizePot = GAME_BUY_IN * numPlayers;
        Game game = Game.builder()
            .id(1)
            .numPlayers(numPlayers)
            .prizePot(prizePot)
            .build();

        Mockito.when(payoutRepository.get(2)).thenReturn(TestConstants.getPayouts(2));

        List<GamePayout> gamePayouts = payoutCalculator.calculate(game);

        Assert.assertNotNull("list of game payouts should not be null", gamePayouts);
        Assert.assertEquals("list of game payouts should be size 2", 2, gamePayouts.size());

        List<Integer> amounts = new ArrayList<>(2);
        int firstPlace = (int)Math.round(0.65 * prizePot);
        amounts.add(firstPlace);
        int secondPlace = (int)Math.round(0.35 * prizePot);
        amounts.add(secondPlace);

        double leftover = prizePot - firstPlace - secondPlace;

        int totalPaidOut = 0;

        for (int i = 0; i < gamePayouts.size(); ++i) {
            GamePayout gamePayout = gamePayouts.get(i);
            int amount = amounts.get(i);
            int place = i+1;

            Assert.assertEquals("payout should be place " + place, place, gamePayout.getPlace());
            Assert.assertEquals(amount, gamePayout.getAmount(), leftover);
            Assert.assertEquals("payout chop should be 0", 0, gamePayout.getChopAmount());
            Assert.assertEquals(0.0, gamePayout.getChopPercent(), 0.0);
            totalPaidOut += gamePayout.getAmount();
        }

        Assert.assertEquals("sum of payouts should be " + prizePot, prizePot, totalPaidOut);
    }

}
