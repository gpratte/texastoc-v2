package com.texastoc.controller;

import com.texastoc.model.supply.Supplies;
import com.texastoc.model.supply.Supply;
import com.texastoc.model.supply.SupplyType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@RestController
public class SupplyRestController {

    @GetMapping("/api/v2/supplies")
    public Supplies getSupplies() {
        Supplies supplies = new Supplies();
        supplies.setKittyAmount(430);

        List<Supply> supplyList = new ArrayList<>();
        supplies.setSupplies(supplyList);

        Supply supply = new Supply();
        supply.setCost(54);
        supply.setDate(LocalDate.now());
        supply.setDescription("copec decks");
        supply.setName("Brian Baker");
        supply.setType(SupplyType.CARDS);
        supplyList.add(supply);

        return supplies;
    }
}
