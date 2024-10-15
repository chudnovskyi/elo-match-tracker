package com.emt.controller;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.emt.ITBase;
import com.emt.entity.Player;
import com.emt.model.exception.PlayerNotFoundException;
import com.emt.repository.PlayerRepository;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.MethodArgumentNotValidException;

@RequiredArgsConstructor
public class PlayerControllerIT extends ITBase {

  private final MockMvc mockMvc;

  private final PlayerRepository userRepository;

  @Test
  void getUserBpId_withPreCreatedUser_expectResponseMatch() throws Exception {
    Player player = Player.builder().playerId(123L).nickname("hopondeadlock@gmail.com").eloRating(1300).build();
    userRepository.save(player);

    mockMvc
        .perform(get("/players/").param("playerId", player.getPlayerId().toString()))
        .andExpectAll(
            status().isOk(),
            jsonPath("$.userId").value(player.getPlayerId()),
            jsonPath("$.firstName").value(player.getNickname()),
            jsonPath("$.lastName").value(player.getEloRating()));
  }

  @Test
  void exceptionHandling_withNonExistingUser_expectUserNotFoundException() throws Exception {
    mockMvc
        .perform(get("/players/").param("playerId", "999"))
        .andExpectAll(
            status().isNotFound(),
            jsonPath("$.type").value(PlayerNotFoundException.class.getSimpleName()),
            jsonPath("$.status").value(404));
  }

  @Test
  void exceptionHandling_badRequestBody_expectMethodArgumentNotValidException() throws Exception {
    mockMvc
        .perform(
            post("/api/v1/users/")
                .content(
                    """
                    {
                      "username": null,
                      "eloRating": null
                    }
                    """)
                .contentType(APPLICATION_JSON))
        .andExpectAll(
            status().isBadRequest(),
            jsonPath("$.type").value(MethodArgumentNotValidException.class.getSimpleName()),
            jsonPath("$.status").value(400),
            jsonPath("$.detail.firstName").value("must not be null"),
            jsonPath("$.detail.lastName").value("must not be null"));
  }
}
