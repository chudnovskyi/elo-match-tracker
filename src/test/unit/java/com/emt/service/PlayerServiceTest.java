package com.emt.service;

import com.emt.entity.Player;
import com.emt.mapper.PlayerMapper;
import com.emt.model.exception.PlayerAlreadyExistsException;
import com.emt.model.request.CreatePlayerRequest;
import com.emt.model.response.PlayerResponse;
import com.emt.repository.PlayerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PlayerServiceTest {

  private static final String NICKNAME = "topOneDeadlocker";

  @Mock private PlayerRepository playerRepository;
  @Mock private PlayerMapper playerMapper;
  @InjectMocks private PlayerService playerService;

  @Test
  void createPlayer_WhenPlayerDoesNotExist_ShouldCreateNewPlayer() {
    CreatePlayerRequest request = CreatePlayerRequest.builder().nickname(NICKNAME).build();
    Player player = new Player(1L, NICKNAME, 2500, Instant.now());
    PlayerResponse expectedResponse =
        PlayerResponse.builder()
            .playerId(1L)
            .nickname(NICKNAME)
            .eloRating(2500)
            .registeredAt(Instant.now())
            .build();

    given(playerRepository.existsByNickname(request.nickname())).willReturn(false);
    given(playerMapper.mapToEntity(request)).willReturn(player);
    given(playerRepository.save(player)).willReturn(player);
    given(playerMapper.mapToResponse(player)).willReturn(expectedResponse);

    PlayerResponse actualResponse = playerService.createPlayer(request);

    assertThat(actualResponse).isEqualTo(expectedResponse);
    verify(playerRepository).save(player);
  }

  @Test
  void createPlayer_WhenPlayerAlreadyExists_ShouldThrowException() {
    CreatePlayerRequest request = CreatePlayerRequest.builder().nickname(NICKNAME).build();

    given(playerRepository.existsByNickname(request.nickname())).willReturn(true);

    assertThatThrownBy(() -> playerService.createPlayer(request))
        .isInstanceOf(PlayerAlreadyExistsException.class)
        .hasMessageContaining("Player with nickname " + NICKNAME + " already exists.");
  }
}