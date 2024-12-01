package com.emt.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.emt.ITBase;
import com.emt.model.request.CreatePlayerRequest;
import com.emt.model.response.MatchResponse;
import com.emt.model.response.PlayerResponse;
import com.emt.service.MatchService;
import com.emt.service.PlayerService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;

@RequiredArgsConstructor
public class MatchControllerIT extends ITBase {

  private final MockMvc mockMvc;
  private final PlayerService playerService;
  private final MatchService matchService;

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
  void createMatch_withIdenticalPlayers_expectIdenticalPlayersException() throws Exception {
    PlayerResponse player =
        playerService.createPlayer(
            CreatePlayerRequest.builder().nickname("duplicatePlayer").build());

    mockMvc
        .perform(
            post("/matches/report")
                .param("winnerId", String.valueOf(player.playerId()))
                .param("loserId", String.valueOf(player.playerId())))
        .andExpectAll(
            status().is3xxRedirection(),
            flash().attribute("errorMessage", "A match cannot be created with identical players."));
  }

  @Test
  void cancelMatch_withValidMatchId_shouldCancelMatch() throws Exception {
    PlayerResponse winner =
        playerService.createPlayer(CreatePlayerRequest.builder().nickname("winner").build());
    PlayerResponse loser =
        playerService.createPlayer(CreatePlayerRequest.builder().nickname("loser").build());

    mockMvc
        .perform(
            post("/matches/report")
                .param("winnerId", String.valueOf(winner.playerId()))
                .param("loserId", String.valueOf(loser.playerId())))
        .andExpect(redirectedUrl("/players"));

    List<MatchResponse> matches = matchService.getAllMatches();
    Long matchId = matches.get(0).matchId();

    mockMvc
        .perform(post("/matches/cancel").param("matchId", String.valueOf(matchId)))
        .andExpect(redirectedUrl("/matches"))
        .andExpect(flash().attribute("message", "Match cancelled successfully!"));

    List<MatchResponse> updatedMatches = matchService.getAllMatches();
    assertThat(updatedMatches).isEmpty();
  }
}
