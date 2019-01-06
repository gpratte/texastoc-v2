package com.texastoc.controller.request;

import com.texastoc.model.game.TableRequest;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@Builder
public class SeatingRequest {
    @NotNull(message = "game id is required")
    private Integer gameId;

    private Integer numDeadStacks;

    private List<TableRequest> tableRequests;
}
