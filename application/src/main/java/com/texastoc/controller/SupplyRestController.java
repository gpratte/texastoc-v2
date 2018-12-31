package com.texastoc.controller;

import com.texastoc.model.supply.Supply;
import com.texastoc.service.SupplyService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class SupplyRestController {

    private final SupplyService supplyService;

    public SupplyRestController(SupplyService supplyService) {
        this.supplyService = supplyService;
    }

    @GetMapping("/api/v2/supplies")
    public List<Supply> getSupplies() {
        return supplyService.get();
    }

    @PostMapping("/api/v2/supplies")
    public String createSupply(@RequestBody Supply supply) {
        supplyService.create(supply);
        return "hi";
    }

}
