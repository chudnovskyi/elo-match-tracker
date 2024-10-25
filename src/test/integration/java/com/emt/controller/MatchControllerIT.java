package com.emt.controller;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.emt.ITBase;
import com.emt.model.request.CreatePlayerRequest;
import com.emt.model.response.PlayerResponse;
import com.emt.service.PlayerService;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

@RequiredArgsConstructor
public class MatchControllerIT extends ITBase {

  private final MockMvc mockMvc;
  private final PlayerService playerService;

  @Test
  void getAllMatches_noMatches_expectEmptyList() throws Exception {
    mockMvc
        .perform(get("/matches"))
        .andExpect(status().isOk())
        .andExpect(view().name("match-history"))
        .andExpect(model().attributeExists("matches"))
        .andExpect(model().attribute("matches", List.of()));
  }

  @Test
  void createMatch_withValidRequest_expectMatchCreated() throws Exception {
    PlayerResponse winner =
        playerService.createPlayer(CreatePlayerRequest.builder().nickname("winner").build());
    PlayerResponse loser =
        playerService.createPlayer(CreatePlayerRequest.builder().nickname("loser").build());

    mockMvc
        .perform(
            post("/matches/report")
                .param("winnerId", String.valueOf(winner.playerId()))
                .param("loserId", String.valueOf(loser.playerId())))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/players"))
        .andExpect(flash().attribute("message", "Match reported successfully!"));
  }

  @Test
  @SuppressWarnings("PMD.AvoidDuplicateLiterals")
  void createMatch_withIdenticalPlayers_expectIdenticalPlayersException() throws Exception {
    PlayerResponse player =
        playerService.createPlayer(
            CreatePlayerRequest.builder().nickname("duplicatePlayer").build());

    assertNotNull(player.playerId(), "PlayerId should not be null");

    mockMvc
        .perform(
            post("/matches/report")
                .param("winnerId", String.valueOf(player.playerId()))
                .param("loserId", String.valueOf(player.playerId())))
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.type").value("IdenticalPlayersException"))
        .andExpect(jsonPath("$.status").value(409))
        .andExpect(jsonPath("$.detail").value("A match cannot be created with identical players."));
  }
}
