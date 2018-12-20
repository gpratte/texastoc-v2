package com.texastoc;

import com.texastoc.model.config.TocConfig;

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
}
