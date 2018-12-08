package com.texastoc.controller;

import com.texastoc.model.game.*;
import com.texastoc.model.game.clock.Clock;
import com.texastoc.model.game.clock.Round;
import com.texastoc.model.season.Quarter;
import com.texastoc.model.season.Season;
import com.texastoc.model.user.Player;
import com.texastoc.service.GameService;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@RestController
public class GameRestController {

    private final GameService gameService;

    public GameRestController(GameService gameService) {
        this.gameService = gameService;
    }

    @PostMapping("/api/v2/games")
    public Game createGame(@RequestBody @Valid Game game) {
        return gameService.createGame(game);
    }

}
