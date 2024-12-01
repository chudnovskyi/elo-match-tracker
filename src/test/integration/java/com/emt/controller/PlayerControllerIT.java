package com.emt.controller;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.emt.ITBase;
import com.emt.model.request.CreatePlayerRequest;
import com.emt.model.response.PlayerResponse;
import com.emt.service.PlayerService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.MethodArgumentNotValidException;

@RequiredArgsConstructor
public class PlayerControllerIT extends ITBase {

  private final MockMvc mockMvc;
  private final PlayerService playerService;

  @Test
  void getPlayers_withPreCreatedPlayer_expectResponseMatch() throws Exception {
    PlayerResponse response =
        playerService.createPlayer(CreatePlayerRequest.builder().nickname("hopondeadlock").build());

    mockMvc
        .perform(get("/players"))
        .andExpectAll(
            status().isOk(),
            view().name("elo-ranking"),
            model().attributeExists("players"),
            model().attributeExists("playerRequest"),
            model().attributeExists("matchRequest"),
            model().attribute("players", List.of(response)));
  }

  @Test
  void exceptionHandling_badRequestBody_expectMethodArgumentNotValidException() throws Exception {
    mockMvc
        .perform(
            post("/players/register").content("{\"nickname\": null}").contentType(APPLICATION_JSON))
        .andExpectAll(status().is3xxRedirection(), flash().attribute("errorMessage", "nickname: Nickname should not be null.<br>"));
  }
}
