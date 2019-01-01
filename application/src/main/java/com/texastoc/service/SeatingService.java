package com.texastoc.service;

import com.texastoc.model.game.Game;
import com.texastoc.model.game.GamePlayer;
import com.texastoc.model.game.Seat;
import com.texastoc.model.game.Table;
import com.texastoc.model.game.TableRequest;
import com.texastoc.repository.GamePlayerRepository;
import com.texastoc.repository.GameRepository;
import com.texastoc.repository.SeatingRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Service
public class SeatingService {

    private final SeatingRepository seatingRepository;
    private final GamePlayerRepository gamePlayerRepository;

    private Random random = new Random(System.currentTimeMillis());

    public SeatingService(SeatingRepository seatingRepository, GamePlayerRepository gamePlayerRepository) {
        this.seatingRepository = seatingRepository;
        this.gamePlayerRepository = gamePlayerRepository;
    }

    public List<Table> get(int gameId) {
        return seatingRepository.get(gameId);
    }

    public List<Table> seat(int gameId, int numDeadStacks, List<TableRequest> tableRequests) {

        List<GamePlayer> currentPlayers = gamePlayerRepository.selectByGameId(gameId);

        List<GamePlayer> playersToRandomize = new ArrayList<GamePlayer>();

        // Add players that are in the game and have a buy in
        for (GamePlayer gamePlayer : currentPlayers) {
            if (gamePlayer.getBuyInCollected() != null && gamePlayer.getBuyInCollected() > 0) {
                playersToRandomize.add(gamePlayer);
            }
        }

        if (playersToRandomize.size() > 2) {
            // Randomize the list quit a bit
            for (int loop = 0; loop < 100; ++loop) {
                // Swap two players
                int index1 = random.nextInt(playersToRandomize.size());
                int index2 = random.nextInt(playersToRandomize.size());
                GamePlayer gamePlayer = playersToRandomize.get(index1);
                playersToRandomize.set(index1, playersToRandomize.get(index2));
                playersToRandomize.set(index2, gamePlayer);
            }
        }

        // Create tables max 10 players
        int numTables = (int)Math.ceil((playersToRandomize.size() + numDeadStacks) / (double)10);
        List<Table> tables = new ArrayList<>(numTables);

        if (numTables < 1) {
            return tables;
        }

        int totalPlayersRemaining = playersToRandomize.size();
        int totalDeadStacksRemaining = numDeadStacks;

        // Create the seats for the tables (1's based)
        for (int i = 0; i < numTables; i++) {
            Table table = Table.builder()
                .number(i+1)
                .gameId(gameId)
                .build();
            tables.add(table);
            List<Seat> seats = new LinkedList<>();
            table.setSeats(seats);
        }

        while ( (totalPlayersRemaining + totalDeadStacksRemaining) > 0 ) {

            // Add players in order
            for (int i = 0; i < tables.size(); i++) {
                if (totalPlayersRemaining > 0) {
                    Table table = tables.get(i);

                    // Get a player
                    GamePlayer gamePlayer = playersToRandomize.get(totalPlayersRemaining - 1);
                    totalPlayersRemaining -= 1;

                    // Add a seat
                    List<Seat> seats = table.getSeats();
                    seats.add(Seat.builder()
                        .gameId(gameId)
                        .number(seats.size() + 1)
                        .gamePlayerId(gamePlayer.getId())
                        .gamePlayerName(gamePlayer.getName())
                        .build());
                }
            }

            // Add dead stack in reverse order
            for (int i = tables.size() - 1; i >= 0; i--) {
                if (totalDeadStacksRemaining > 0) {
                    Table table = tables.get(i);
                    totalDeadStacksRemaining -= 1;

                    // Add a seat
                    List<Seat> seats = table.getSeats();
                    seats.add(Seat.builder()
                        .gameId(gameId)
                        .number(seats.size() + 1)
                        .gamePlayerName("Dead Stack")
                        .build());
                }
            }
        }

        if (tableRequests != null) {
            for (TableRequest tableRequest : tableRequests) {
                Table table = tables.get(tableRequest.getTableNum() - 1);
                boolean found = false;
                for (Seat seat : table.getSeats()) {
                    if (seat.getGamePlayerId() != null && seat.getGamePlayerId() == tableRequest.getGamePlayerId()) {
                        found = true;
                        break;
                    }
                }

                if (!found) {
                    
                }
            }
        }

        seatingRepository.deleteByGameId(gameId);
        for (Table table : tables) {
            seatingRepository.save(table);
        }

        return tables;

//        // If the player to be at table 1 and that player is at another
//        // table then swap that player with someone at table 1
//        if (firstPlayer != null) {
//            int tableToSwap = 0;
//            int seatToSwap = 0;
//            outer:
//            for (int i = 2; i <= numTables; ++i) {
//                List<Seat> seats = tables.get(i);
//                for (int j = 0; j < seats.size(); ++j) {
//                    if (seats.get(j).getPlayer() != null &&
//                        seats.get(j).getPlayer().getId() == firstPlayer.getId()) {
//                        tableToSwap = i;
//                        seatToSwap = j;
//                        break outer;
//                    }
//                }
//            }
//
//            if (tableToSwap > 1) {
//                // Get the seat where the player is that will be moved
//                // to table 1
//                Seat originalSeat = tables.get(tableToSwap).remove(seatToSwap);
//
//                // Randomly pick a seat at table 1
//                int table1SeatIndex = -1;
//                List<Seat> table1Seats = tables.get(1);
//                do {
//                    table1SeatIndex = RANDOM.nextInt(tables.get(1).size());
//                } while (table1Seats.get(table1SeatIndex).getPlayer() == null);
//
//                Seat seatToMoveFromTable1 = tables.get(1).remove(table1SeatIndex);
//
//                originalSeat.setTable(1);
//                tables.get(1).add(table1SeatIndex, originalSeat);
//
//                seatToMoveFromTable1.setTable(tableToSwap);
//                tables.get(tableToSwap).add(seatToSwap, seatToMoveFromTable1);
//            }
//        }
//
//        // Now place the players in the seats at each table
//        for (int i = 1; i <= numTables; ++i) {
//            List<Seat> seats = tables.get(i);
//
//            int numPlayersSeated = 0;
//            for (Seat seat : seats) {
//                if (seat.getPlayer() != null) {
//                    ++numPlayersSeated;
//                }
//            }
//
//            double seatingFactor = seats.size() / (double)numPlayersSeated;
//            seats.get(0).setPosition(1);
//
//            double nextSeat = 1;
//
//            for (int j = 1; j < numPlayersSeated; ++j) {
//                nextSeat += seatingFactor;
//                seats.get(j).setPosition((int) Math.round(nextSeat));
//            }
//        }
//
//        List<Seat> allSeats = new ArrayList<Seat>();
//        for (int i = 1; i <= numTables; ++i) {
//            List<Seat> seats = tables.get(i);
//            for (Seat seat : seats) {
//                if (seat.getPlayer() == null) {
//                    continue;
//                }
//                allSeats.add(seat);
//            }
//        }
//
//        gameSeating.put(gameId, allSeats);
//        gameTables.put(gameId, playersPerTable);

    }

}
