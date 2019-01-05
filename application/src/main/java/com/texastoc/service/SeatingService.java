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

    public List<Table> seat(int gameId, Integer numDeadStacks, List<TableRequest> tableRequests) {

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

        numDeadStacks = numDeadStacks == null ? 0 : numDeadStacks;

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
                        .seatNumber(seats.size() + 1)
                        .tableNumber(table.getNumber())
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
                        .seatNumber(seats.size() + 1)
                        .tableNumber(table.getNumber())
                        .gamePlayerName("Dead Stack")
                        .build());
                }
            }
        }

        if (tableRequests != null) {

            // Go through the requests
            for (TableRequest tableRequest : tableRequests) {

                // Find the seat of the player that wants to swap
                Seat playerThatWantsToSwapSeat = null;
                tableLoop: for (Table table : tables) {
                    for (Seat seat : table.getSeats()) {
                        if (seat.getGamePlayerId() != null && seat.getGamePlayerId() == tableRequest.getGamePlayerId()) {
                            playerThatWantsToSwapSeat = seat;
                            break tableLoop;
                        }
                    }
                }

                if (playerThatWantsToSwapSeat == null) {
                    // Should never happen
                    throw new RuntimeException("Cannot find the seat of the player that want to swap");
                }

                Table tableToMoveTo = tables.get(tableRequest.getTableNum() - 1);

                // See if player is already at that table
                if (playerThatWantsToSwapSeat.getTableNumber() != tableToMoveTo.getNumber()) {
                    for (Seat seatAtTableToMoveTo : tableToMoveTo.getSeats()) {
                        // Find a seat to swap - avoid seats that are dead stacks and avoid seats of players that have already been swapped
                        if (seatAtTableToMoveTo.getGamePlayerId() != null && !seatBelongsToPlayerThatRequestedTheTable(seatAtTableToMoveTo.getGamePlayerId(), tableRequests)) {
                            // Swap
                            int saveGamePlayerId = seatAtTableToMoveTo.getGamePlayerId();
                            String saveGamePlayerName = seatAtTableToMoveTo.getGamePlayerName();

                            seatAtTableToMoveTo.setGamePlayerId(playerThatWantsToSwapSeat.getGamePlayerId());
                            seatAtTableToMoveTo.setGamePlayerName(playerThatWantsToSwapSeat.getGamePlayerName());

                            playerThatWantsToSwapSeat.setGamePlayerId(saveGamePlayerId);
                            playerThatWantsToSwapSeat.setGamePlayerName(saveGamePlayerName);
                            break;
                        }
                    }
                }
            }
        }

        seatingRepository.deleteByGameId(gameId);
        for (Table table : tables) {
            seatingRepository.save(table);
        }

        return tables;
    }

    private boolean seatBelongsToPlayerThatRequestedTheTable(int gamePlayerId, List<TableRequest> tableRequests) {
        for (TableRequest tableRequest : tableRequests) {
            if (tableRequest.getGamePlayerId() == gamePlayerId) {
                return true;
            }
        }
        return false;
    }
}
