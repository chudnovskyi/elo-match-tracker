package com.emt.service;

import com.emt.entity.Match;
import com.emt.entity.Player;
import com.emt.mapper.MatchMapper;
import com.emt.model.exception.IdenticalPlayersException;
import com.emt.model.request.CreateMatchRequest;
import com.emt.model.response.MatchResponse;
import com.emt.repository.MatchRepository;
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
class MatchServiceTest {

  @Mock private PlayerService playerService;

  @Mock private MatchRepository matchRepository;

  @Mock private MatchMapper matchMapper;

  @InjectMocks private MatchService matchService;

  @Test
  @SuppressWarnings("PMD.AvoidDuplicateLiterals")
  void createMatch_WhenWinnerAndLoserAreIdentical_ShouldThrowException() {
    CreateMatchRequest request = CreateMatchRequest.builder().winnerId(1L).loserId(1L).build();

    assertThatThrownBy(() -> matchService.createMatch(request))
        .isInstanceOf(IdenticalPlayersException.class)
        .hasMessageContaining("A match cannot be created with identical players.");
  }

  @Test
  void createMatch_WhenPlayersAreDifferent_ShouldCreateMatchSuccessfully() {
    CreateMatchRequest request = CreateMatchRequest.builder().winnerId(1L).loserId(2L).build();

    Player winner = new Player(1L, "WinnerPlayer", 2500, Instant.now());
    Player loser = new Player(2L, "LoserPlayer", 2000, Instant.now());
    Match match = new Match();
    MatchResponse expectedResponse =
        new MatchResponse("WinnerPlayer", "LoserPlayer", Instant.now());

    given(playerService.getReferenceById(1L)).willReturn(winner);
    given(playerService.getReferenceById(2L)).willReturn(loser);
    given(matchMapper.mapToEntity(winner, loser)).willReturn(match);
    given(matchRepository.save(match)).willReturn(match);
    given(matchMapper.mapToResponse(match)).willReturn(expectedResponse);

    MatchResponse actualResponse = matchService.createMatch(request);

    assertThat(actualResponse).isEqualTo(expectedResponse);
    verify(matchRepository).save(match);
  }
}
