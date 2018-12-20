package com.texastoc.model.config;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
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
