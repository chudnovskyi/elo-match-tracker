package com.emt.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.emt.ITBase;
import com.emt.entity.Player;
import com.emt.mapper.PlayerMapper;
import com.emt.model.request.CreatePlayerRequest;
import com.emt.model.response.PlayerResponse;
import com.emt.repository.PlayerRepository;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@RequiredArgsConstructor
public class PlayerServiceIT extends ITBase {

  private static final String NICKNAME = "top4ik";
  private final PlayerRepository playerRepository;
  private final PlayerService playerService;
  private final PlayerMapper playerMapper;

  @BeforeEach
  public void setUp() {
    playerRepository.deleteAll();
    playerRepository.save(new Player(1L, "Player1", new BigDecimal("1500"), Instant.now()));
    playerRepository.save(new Player(2L, "Player2", new BigDecimal("1800"), Instant.now()));
    playerRepository.save(new Player(3L, "Player3", new BigDecimal("1700"), Instant.now()));
  }

  @Test
  public void testGetAllPlayers_SortedByEloRating() {
    List<PlayerResponse> players = playerService.getAllPlayers();

    assertThat(players).hasSize(3);
    assertThat(players.get(0).nickname()).isEqualTo("Player2");
    assertThat(players.get(1).nickname()).isEqualTo("Player3");
    assertThat(players.get(2).nickname()).isEqualTo("Player1");

    assertThat(players.get(0).eloRating()).isEqualTo("1800");
    assertThat(players.get(1).eloRating()).isEqualTo("1700");
    assertThat(players.get(2).eloRating()).isEqualTo("1500");
  }

  @Test
  public void testCreatePlayer_Success() {
    PlayerResponse actualPlayer =
        playerService.createPlayer(CreatePlayerRequest.builder().nickname(NICKNAME).build());

    assertThat(actualPlayer).isNotNull();
    assertThat(actualPlayer.nickname()).isEqualTo(NICKNAME);

    Player savedPlayer = playerRepository.findById(actualPlayer.playerId()).orElseThrow();

    Player expectedPlayer =
        new Player(
            savedPlayer.getPlayerId(),
            NICKNAME,
            new BigDecimal("1200"),
            savedPlayer.getRegisteredAt());
    PlayerResponse expectedResponse = playerMapper.mapToResponse(expectedPlayer);

    assertThat(savedPlayer.getNickname()).isEqualTo(expectedPlayer.getNickname());
    assertThat(actualPlayer.playerId()).isEqualTo(expectedResponse.playerId());
    assertThat(actualPlayer.nickname()).isEqualTo(expectedResponse.nickname());
    assertThat(actualPlayer.registeredAt()).isEqualTo(expectedResponse.registeredAt());
    assertThat(actualPlayer.eloRating()).isEqualByComparingTo(expectedResponse.eloRating());
  }
}
