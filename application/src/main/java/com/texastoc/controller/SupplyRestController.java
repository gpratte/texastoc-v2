package com.texastoc.controller;

import com.texastoc.model.supply.Supply;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class SupplyRestController {

    @GetMapping("/api/v2/supplies")
    public List<Supply> getSupplies() {
        return null;
    }
}
