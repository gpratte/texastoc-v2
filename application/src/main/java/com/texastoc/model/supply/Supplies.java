package com.texastoc.model.supply;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class Supplies {
    private int kittyAmount;
    private List<Supply> supplies;

    public void addSupply(Supply supply) {
        if (supplies == null) {
            supplies = new ArrayList<>();
        }
        supplies.add(supply);
    }
}
