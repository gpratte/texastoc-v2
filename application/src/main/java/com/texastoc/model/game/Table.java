package com.texastoc.model.game;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Table {

    private int gameId;
    private int number;
    private List<Seat> seats;

    public void addSeat(Seat seat) {
        if (seats == null) {
            seats = new ArrayList<>();
        }
        seats.add(seat);
    }
}
