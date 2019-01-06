package com.texastoc.model.config;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class TocConfig {

    private Integer kittyDebit;
    private Integer annualTocCost;
    private Integer quarterlyTocCost;
    private Integer quarterlyNumPayouts;
    private Integer regularBuyInCost;
    private Integer regularRebuyCost;
    private Integer regularRebuyTocDebit;
    private Integer doubleBuyInCost;
    private Integer doubleRebuyCost;
    private Integer doubleRebuyTocDebit;

}
