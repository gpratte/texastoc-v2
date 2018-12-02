package com.texastoc.controller;

import com.texastoc.model.game.*;
import com.texastoc.model.game.clock.Clock;
import com.texastoc.model.game.clock.Round;
import com.texastoc.model.user.Player;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@RestController
public class GameRestController {

    @GetMapping("/api/v2/games/season/{id}")
    public List<Game> getGamesOfSeason() {

        List<Game> games = new ArrayList<>();
        Game game = new Game();
        populateGame(game);
        games.add(game);

        return games;
    }

    @GetMapping("/api/v2/games/current")
    public CurrentGame getCurrent() {

        CurrentGame game = new CurrentGame();
        populateGame(game);
        game.setClockId(3);
        game.setTransportRequired(true);

        Table table = new Table();
        game.addTable(table);
        table.setNumber(1);

        Seat seat = new Seat();
        seat.setNumber(1);
        seat.setGamePlayerId(3);
        seat.setGamePlayerName("Joey Baloney");
        table.addSeat(seat);

        seat = new Seat();
        seat.setNumber(2);
        seat.setGamePlayerId(5);
        seat.setGamePlayerName("Bob your uncle");
        table.addSeat(seat);

        return game;
    }

    @PutMapping("/api/v2/games/current")
    public void updateCurrent(@RequestBody(required=false) UpdateGame updateGame) {
        System.out.println(updateGame);
    }

    @GetMapping("/api/v2/games/current/players")
    public List<Player> getGamePlayers(@RequestParam Boolean included) {
        System.out.println(included);
        List<Player> players = new ArrayList<>();

        Player player = new Player();
        player.setId(1);
        player.setName("Brian Baker");
        players.add(player);

        player = new Player();
        player.setId(2);
        player.setName("John Hiatt");
        players.add(player);

        return players;
    }

    @PostMapping("/api/v2/games/current/seating")
    public List<Table> seatPlayers(@RequestBody(required=false) Seating seating) {
        System.out.println(seating);

        List<Table> tables = new ArrayList<>();
        Table table = new Table();
        tables.add(table);
        table.setNumber(1);

        Seat seat = new Seat();
        seat.setNumber(1);
        seat.setGamePlayerId(3);
        seat.setGamePlayerName("Joey Baloney");
        table.addSeat(seat);

        seat = new Seat();
        seat.setNumber(2);
        seat.setGamePlayerId(5);
        seat.setGamePlayerName("Bob your uncle");
        table.addSeat(seat);

        return tables;
    }

    @GetMapping("/api/v2/games/current/clock")
    public Clock getCurrentClock() {
        Clock clock = new Clock();
        clock.setClockId(1);
        clock.setMinutes(20);
        clock.setSeconds(0);
        clock.setPlaying(false);

        Round round = new Round();
        round.setBigBlind(50);
        round.setSmallBlind(25);
        round.setAnte(0);
        round.setRound(1);
        clock.setThisRound(round);

        round = new Round();
        round.setBigBlind(100);
        round.setSmallBlind(50);
        round.setAnte(0);
        round.setRound(2);
        clock.setNextRound(round);

        return clock;
    }

    @PutMapping("/api/v2/games/current/clock")
    public Clock updateCurrentClock(@RequestBody ClockControls clockControls) {
        System.out.println(clockControls);
        Clock clock = new Clock();
        clock.setClockId(1);
        clock.setMinutes(20);
        clock.setSeconds(0);
        clock.setPlaying(false);

        Round round = new Round();
        round.setBigBlind(50);
        round.setSmallBlind(25);
        round.setAnte(0);
        round.setRound(1);
        clock.setThisRound(round);

        round = new Round();
        round.setBigBlind(100);
        round.setSmallBlind(50);
        round.setAnte(0);
        round.setRound(2);
        clock.setNextRound(round);

        return clock;
    }

    private void populateGame(Game game) {
        game.setId(1);
        game.setAnnualTocAmount(340);
        game.setBuyIn(680);
        game.setDate(LocalDate.now());
        game.setDoubleBuyIn(false);
        game.setHostId(4);
        game.setKitty(10);
        game.setHostName("Brian Baker");
        game.setNumPlayers(2);
        game.setQuarterlyOrdinal(1);
        game.setQuarterlyTocAmount(120);
        game.setRebuyAddOn(200);
        game.setSeasonOrdinal(1);
        game.setStart(LocalTime.now());

        GamePlayer player = new GamePlayer();
        player.setAnnualToc(10);
        player.setBuyIn(30);
        player.setId(4);
        player.setName("Brian Baker");
        player.setPlace(1);
        player.setPoints(124);
        player.setQuarterlyToc(10);
        player.setReBuy(20);
        game.addPlayer(player);

        player = new GamePlayer();
        player.setBuyIn(30);
        player.setId(5);
        player.setName("John Hiatt");
        game.addPlayer(player);

        GamePayout payout = new GamePayout();
        payout.setAmount(100);
        payout.setPlace(1);
        game.addPayout(payout);

        payout = new GamePayout();
        payout.setAmount(85);
        payout.setPlace(2);
        game.addPayout(payout);

    }

    @Getter
    @Setter
    @NoArgsConstructor
    @ToString
    private static class UpdateGame {
        Integer hostId;
        Boolean transportRequired;
        Boolean finished;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @ToString
    private static class Seating {
        int numTables;
        int numSeatsPerTable;
        List<ToSeat> seats;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @ToString
    private static class ToSeat {
        int playerId;
        int table;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @ToString
    private static class ClockControls {
        private boolean play;
        private boolean nextRound;
        private boolean previousRound;
        private int minutes;
        private int seconds;
    }

}
