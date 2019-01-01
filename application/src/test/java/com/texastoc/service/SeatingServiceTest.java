package com.texastoc.service;

import com.texastoc.TestConstants;
import com.texastoc.model.game.GamePlayer;
import com.texastoc.model.game.Seat;
import com.texastoc.model.game.Table;
import com.texastoc.model.game.TableRequest;
import com.texastoc.model.supply.Supply;
import com.texastoc.model.supply.SupplyType;
import com.texastoc.repository.GamePlayerRepository;
import com.texastoc.repository.GameRepository;
import com.texastoc.repository.SeatingRepository;
import com.texastoc.repository.SupplyRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

@RunWith(SpringRunner.class)
public class SeatingServiceTest implements TestConstants {

    private SeatingService seatingService;

    @MockBean
    private SeatingRepository seatingRepository;
    @MockBean
    private GamePlayerRepository gamePlayerRepository;

    @Before
    public void before() {
        seatingService = new SeatingService(seatingRepository, gamePlayerRepository);
    }

    @Test
    public void testNotSeated() {

        Mockito.when(seatingRepository.get(1)).thenReturn(Collections.emptyList());

        List<Table> tables = seatingService.get(1);

        Mockito.verify(seatingRepository, Mockito.times(1)).get(1);

        Assert.assertNotNull("tables should not be null", tables);
        Assert.assertEquals("number of tables 0", 0, tables.size());
    }

    @Test
    public void test2TableSeated() {

        List<Table> currentTables = new ArrayList<>(2);
        List<Seat> currentSeats = new ArrayList<>(4);
        currentSeats.add(Seat.builder()
            .gameId(1)
            .gamePlayerId(1)
            .gamePlayerName("One")
            .seatNumber(1)
            .tableNumber(1)
            .build());
        currentSeats.add(Seat.builder()
            .gameId(1)
            .gamePlayerId(2)
            .gamePlayerName("Two")
            .seatNumber(2)
            .tableNumber(1)
            .build());
        currentSeats.add(Seat.builder()
            .gameId(1)
            .gamePlayerId(3)
            .gamePlayerName("Three")
            .seatNumber(3)
            .tableNumber(1)
            .build());
        currentSeats.add(Seat.builder()
            .gameId(1)
            .gamePlayerId(4)
            .gamePlayerName("Four")
            .seatNumber(4)
            .tableNumber(1)
            .build());

        Table table = Table.builder()
            .gameId(1)
            .number(1)
            .seats(currentSeats)
            .build();
        currentTables.add(table);

        currentSeats = new ArrayList<>(4);
        currentSeats.add(Seat.builder()
            .gameId(1)
            .gamePlayerId(5)
            .gamePlayerName("Five")
            .seatNumber(1)
            .tableNumber(2)
            .build());
        currentSeats.add(Seat.builder()
            .gameId(1)
            .gamePlayerId(6)
            .gamePlayerName("Six")
            .seatNumber(2)
            .tableNumber(2)
            .build());
        currentSeats.add(Seat.builder()
            .gameId(1)
            .gamePlayerId(7)
            .gamePlayerName("Seven")
            .seatNumber(3)
            .tableNumber(2)
            .build());

        table = Table.builder()
            .gameId(1)
            .number(2)
            .seats(currentSeats)
            .build();
        currentTables.add(table);

        Mockito.when(seatingRepository.get(1)).thenReturn(currentTables);

        List<Table> tables = seatingService.get(1);

        Mockito.verify(seatingRepository, Mockito.times(1)).get(1);

        Assert.assertNotNull("tables should not be null", tables);
        Assert.assertEquals("number of tables 2", 2, tables.size());

        Table firstTable = tables.get(0);
        Assert.assertNotNull("seats for table 1 should not be null", firstTable.getSeats());
        Assert.assertNotNull("seats for table 1 should be 4", firstTable.getSeats().size());

        Table secondTable = tables.get(1);
        Assert.assertNotNull("seats for table 2 should not be null", secondTable.getSeats());
        Assert.assertNotNull("seats for table 2 should be 3", secondTable.getSeats().size());

    }

    @Test
    public void testSeatNoPlayers() {

        Mockito.when(gamePlayerRepository.selectByGameId(1)).thenReturn(Collections.emptyList());

        List<Table> tables = seatingService.seat(1, 0, null);

        Mockito.verify(gamePlayerRepository, Mockito.times(1)).selectByGameId(1);

        Assert.assertNotNull("tables should not be null", tables);
        Assert.assertEquals("number of tables 0", 0, tables.size());
    }

    @Test
    public void testSeat2PlayersNoDeadStacks() {

        List<GamePlayer> gamePlayers = new ArrayList<>(2);
        gamePlayers.add(GamePlayer.builder()
            .id(1)
            .name("one")
            .buyInCollected(10)
            .build());
        gamePlayers.add(GamePlayer.builder()
            .id(2)
            .name("two")
            .buyInCollected(10)
            .build());

        Mockito.when(gamePlayerRepository.selectByGameId(1)).thenReturn(gamePlayers);

        List<Table> tables = seatingService.seat(1, 0, null);

        Mockito.verify(gamePlayerRepository, Mockito.times(1)).selectByGameId(1);

        Mockito.verify(seatingRepository, Mockito.times(1)).deleteByGameId(1);
        Mockito.verify(seatingRepository, Mockito.times(1)).save(Mockito.any(Table.class));

        Assert.assertNotNull("tables should not be null", tables);
        Assert.assertEquals("number of tables 1", 1, tables.size());

        Table firstTable = tables.get(0);
        Assert.assertNotNull("seats for table 1 should not be null", firstTable.getSeats());
        Assert.assertNotNull("seats for table 1 should be 2", firstTable.getSeats().size());

    }

    @Test
    public void testSeat9Players3DeadStacks() {

        List<GamePlayer> gamePlayers = new ArrayList<>(2);
        for (int i = 0; i < 9; i++) {
            gamePlayers.add(GamePlayer.builder()
                .id(i)
                .name("name"+i)
                .buyInCollected(10)
                .build());
        }

        Mockito.when(gamePlayerRepository.selectByGameId(1)).thenReturn(gamePlayers);

        List<Table> tables = seatingService.seat(1, 3, null);

        Mockito.verify(gamePlayerRepository, Mockito.times(1)).selectByGameId(1);

        Mockito.verify(seatingRepository, Mockito.times(1)).deleteByGameId(1);
        Mockito.verify(seatingRepository, Mockito.times(2)).save(Mockito.any(Table.class));

        Assert.assertNotNull("tables should not be null", tables);
        Assert.assertEquals("number of tables 2", 2, tables.size());

        Table firstTable = tables.get(0);
        Assert.assertNotNull("seats for table 1 should not be null", firstTable.getSeats());
        Assert.assertNotNull("seats for table 1 should be 6", firstTable.getSeats().size());

        int countDeads = 0;
        for (Seat seat : firstTable.getSeats()) {
            if ("Dead Stack".equals(seat.getGamePlayerName())) {
                ++countDeads;
            }
        }
        Assert.assertEquals("table 1 should have 1 dead stack", 1, countDeads);

        Table secondTable = tables.get(1);
        Assert.assertNotNull("seats for table 2 should not be null", secondTable.getSeats());
        Assert.assertNotNull("seats for table 2 should be 6", secondTable.getSeats().size());

        countDeads = 0;
        for (Seat seat : secondTable.getSeats()) {
            if ("Dead Stack".equals(seat.getGamePlayerName())) {
                ++countDeads;
            }
        }
        Assert.assertEquals("table 2 should have 2 dead stack", 2, countDeads);

    }

    @Test
    public void testSeat11Players6AtTable1() {

        List<GamePlayer> gamePlayers = new ArrayList<>(2);
        for (int i = 0; i < 11; i++) {
            gamePlayers.add(GamePlayer.builder()
                .id(i)
                .name("name"+i)
                .buyInCollected(10)
                .build());
        }

        Mockito.when(gamePlayerRepository.selectByGameId(1)).thenReturn(gamePlayers);

        List<TableRequest> tableRequests = new ArrayList<>(6);
        for (int i = 0; i < 6; i++) {
            tableRequests.add(TableRequest.builder()
                .gamePlayerId(i)
                .tableNum(1)
                .build());
        }

        List<Table> tables = seatingService.seat(1, 0, tableRequests);

        Mockito.verify(gamePlayerRepository, Mockito.times(1)).selectByGameId(1);

        Mockito.verify(seatingRepository, Mockito.times(1)).deleteByGameId(1);
        Mockito.verify(seatingRepository, Mockito.times(2)).save(Mockito.any(Table.class));

        Assert.assertNotNull("tables should not be null", tables);
        Assert.assertEquals("number of tables 2", 2, tables.size());

        Table firstTable = tables.get(0);
        Assert.assertNotNull("seats for table 1 should not be null", firstTable.getSeats());
        Assert.assertNotNull("seats for table 1 should be 6", firstTable.getSeats().size());

        for (int i = 0; i < 6; i++) {
            boolean found = false;
            for (Seat seat : firstTable.getSeats()) {
                if (seat.getGamePlayerId() == i) {
                    found = true;
                }
            }
            Assert.assertTrue("should have found game player " + i + " at table 1", found);
        }
    }
}
