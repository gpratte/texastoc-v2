package com.texastoc.model.game;

import java.util.List;

public class Seating {
    private int id;
    private int gameId;
    private int numTables;
    private int numSeatPerTable;
    private List<SeatRequest> seatRequests;
    private List<Table> tables;
}
