package com.texastoc.model.game;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class CurrentGame extends Game {

    private int clockId;
    private Boolean transportRequired;
    private List<Table> tables;

    public void addTable(Table table) {
        if (tables == null) {
            tables = new ArrayList<>();
        }
        tables.add(table);
    }
}
