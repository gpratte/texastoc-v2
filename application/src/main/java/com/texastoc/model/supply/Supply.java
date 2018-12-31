package com.texastoc.model.supply;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Supply {
    private int id;
    private LocalDate date;
    private SupplyType type;
    private int amount;
    private String description;
}
