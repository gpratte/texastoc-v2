package com.texastoc;

import com.texastoc.model.common.Payout;
import com.texastoc.model.config.TocConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface TestConstants {

    int KITTY_PER_GAME = 9;
    int TOC_PER_GAME = 8;
    int QUARTERLY_TOC_PER_GAME = 7;
    int QUARTERLY_NUM_PAYOUTS = 3;

    int GAME_BUY_IN = 6;
    int GAME_REBUY = 5;
    int GAME_REBUY_TOC_DEBIT = 4;

    int GAME_DOUBLE_BUY_IN = 12;
    int GAME_DOUBLE_REBUY = 10;
    int GAME_DOUBLE_REBUY_TOC_DEBIT = 8;

    int BRIAN_BAKER_PLAYER_ID = 1;
    String BRIAN_BAKER_NAME = "Brian Baker";

    static TocConfig getTocConfig() {

        return TocConfig.builder()
            .kittyDebit(KITTY_PER_GAME)
            .annualTocCost(TOC_PER_GAME)
            .quarterlyTocCost(QUARTERLY_TOC_PER_GAME)
            .quarterlyNumPayouts(QUARTERLY_NUM_PAYOUTS)
            .regularBuyInCost(GAME_BUY_IN)
            .regularRebuyCost(GAME_REBUY)
            .regularRebuyTocDebit(GAME_REBUY_TOC_DEBIT)
            .doubleBuyInCost(GAME_DOUBLE_BUY_IN)
            .doubleRebuyCost(GAME_DOUBLE_REBUY)
            .doubleRebuyTocDebit(GAME_DOUBLE_REBUY_TOC_DEBIT)
            .build();
    }

    static List<Payout> getPayouts(int num) {

        if (num == 2) {
            List<Payout> payouts = new ArrayList<>(2);
            payouts.add(Payout.builder()
                .place(1)
                .percent(0.65)
                .build());
            payouts.add(Payout.builder()
                .place(2)
                .percent(0.35)
                .build());
            return payouts;
        } else if (num == 3) {
            List<Payout> payouts = new ArrayList<>(3);
            payouts.add(Payout.builder()
                .place(1)
                .percent(0.50)
                .build());
            payouts.add(Payout.builder()
                .place(2)
                .percent(0.30)
                .build());
            payouts.add(Payout.builder()
                .place(3)
                .percent(0.20)
                .build());
            return payouts;
        } else if (num == 4) {
            List<Payout> payouts = new ArrayList<>(4);
            payouts.add(Payout.builder()
                .place(1)
                .percent(0.45)
                .build());
            payouts.add(Payout.builder()
                .place(2)
                .percent(0.25)
                .build());
            payouts.add(Payout.builder()
                .place(3)
                .percent(0.18)
                .build());
            payouts.add(Payout.builder()
                .place(4)
                .percent(0.12)
                .build());
            return payouts;
        } else if (num == 5) {
            List<Payout> payouts = new ArrayList<>(5);
            payouts.add(Payout.builder()
                .place(1)
                .percent(0.40)
                .build());
            payouts.add(Payout.builder()
                .place(2)
                .percent(0.23)
                .build());
            payouts.add(Payout.builder()
                .place(3)
                .percent(0.16)
                .build());
            payouts.add(Payout.builder()
                .place(4)
                .percent(0.12)
                .build());
            payouts.add(Payout.builder()
                .place(5)
                .percent(0.09)
                .build());
            return payouts;
        } else if (num == 6) {
            List<Payout> payouts = new ArrayList<>(6);
            payouts.add(Payout.builder()
                .place(1)
                .percent(0.38)
                .build());
            payouts.add(Payout.builder()
                .place(2)
                .percent(0.22)
                .build());
            payouts.add(Payout.builder()
                .place(3)
                .percent(0.15)
                .build());
            payouts.add(Payout.builder()
                .place(4)
                .percent(0.11)
                .build());
            payouts.add(Payout.builder()
                .place(5)
                .percent(0.08)
                .build());
            payouts.add(Payout.builder()
                .place(6)
                .percent(0.06)
                .build());
            return payouts;
        } else if (num == 7) {
            List<Payout> payouts = new ArrayList<>(7);
            payouts.add(Payout.builder()
                .place(1)
                .percent(0.35)
                .build());
            payouts.add(Payout.builder()
                .place(2)
                .percent(0.21)
                .build());
            payouts.add(Payout.builder()
                .place(3)
                .percent(0.15)
                .build());
            payouts.add(Payout.builder()
                .place(4)
                .percent(0.11)
                .build());
            payouts.add(Payout.builder()
                .place(5)
                .percent(0.08)
                .build());
            payouts.add(Payout.builder()
                .place(6)
                .percent(0.06)
                .build());
            payouts.add(Payout.builder()
                .place(7)
                .percent(0.04)
                .build());
            return payouts;
        }

        throw new RuntimeException("no payouts found for " + num);
    }


}