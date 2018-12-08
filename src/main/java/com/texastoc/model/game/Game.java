package com.texastoc.model.game;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.texastoc.model.season.Quarter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Game {

    private Integer id;
    private Integer seasonId;
    private Integer qSeasonId;
    @NotNull(message = "date is required")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;
    private LocalDateTime started;
    @NotNull(message = "host id is required")
    private Integer hostId;
    private String hostName;
    private Quarter quarter;

    // Game setup variables
    private Boolean doubleBuyIn;
    private Boolean transportSupplies;
    private Integer kittyCost;
    private Integer buyInCost;
    private Integer rebuyAddOnCost;
    private Integer annualTocCost;
    private Integer quarterlyTocCost;

    // Game time variables
    private Integer numPlayers;
    private Integer kittyCollected;
    private Integer buyInCollected;
    private Integer rebuyAddOnCollected;
    private Integer annualTocCollected;
    private Integer quarterlyTocCollected;

    private List<GamePlayer> players;
    private List<GamePayout> payouts;

    public void addPlayer(GamePlayer player) {
        if (players == null) {
            players = new LinkedList<>();
        }
        players.add(player);
    }

    public void addPayout(GamePayout payout) {
        if (payouts == null) {
            payouts = new LinkedList<>();
        }
        payouts.add(payout);
    }
}
