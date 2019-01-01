package com.texastoc.controller.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.texastoc.model.game.TableRequest;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class SeatingRequest {
    @NotNull(message = "game id is required")
    private Integer gameId;

    @NotNull(message = "game player ids is required")
    private List<Integer> gamePlayerIds;

    private List<TableRequest> tableRequests;
}
