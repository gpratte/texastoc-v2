package com.texastoc.model.supply;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class Supply {
    private LocalDate date;
    private String name;
    private int cost;
    private String description;
    private SupplyType type;
}
