package com.emt.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.emt.entity.Match;
import com.emt.entity.Player;
import com.emt.mapper.MatchMapper;
import com.emt.model.exception.IdenticalPlayersException;
import com.emt.model.request.CreateMatchRequest;
import com.emt.model.response.MatchResponse;
import com.emt.repository.MatchRepository;
import java.math.BigDecimal;
import java.time.Instant;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MatchServiceTest {

  private static final BigDecimal CONSTANT_K = new BigDecimal("30");
  @Mock private PlayerService playerService;
  @Mock private MatchRepository matchRepository;
  @Mock private MatchMapper matchMapper;
  @InjectMocks private MatchService matchService;

  @Test
  void createMatch_WhenWinnerAndLoserAreIdentical_ShouldThrowException() {
    CreateMatchRequest request = CreateMatchRequest.builder().winnerId(1L).loserId(1L).build();

    assertThatThrownBy(() -> matchService.createMatch(request))
        .isInstanceOf(IdenticalPlayersException.class)
        .hasMessageContaining("A match cannot be created with identical players.");
  }

  @Test
  void createMatch_WhenPlayersAreDifferent_ShouldCreateMatchSuccessfully() {
    CreateMatchRequest request = CreateMatchRequest.builder().winnerId(1L).loserId(2L).build();

    Player winner = new Player(1L, "WinnerPlayer", new BigDecimal("2500"), Instant.now());
    Player loser = new Player(2L, "LoserPlayer", new BigDecimal("2000"), Instant.now());

    BigDecimal probabilityWinner =
        matchService.calculateProbability(winner.getEloRating(), loser.getEloRating());
    BigDecimal winnerRatingGain =
        CONSTANT_K
            .multiply(BigDecimal.ONE.subtract(probabilityWinner))
            .setScale(2, BigDecimal.ROUND_HALF_UP);
    Match match = new Match();
    MatchResponse expectedResponse =
        new MatchResponse(1L, "WinnerPlayer", "LoserPlayer", Instant.now(), winnerRatingGain);

    given(playerService.getPlayerById(1L)).willReturn(winner);
    given(playerService.getPlayerById(2L)).willReturn(loser);
    given(matchMapper.mapToEntity(winner, loser, winnerRatingGain)).willReturn(match);
    given(matchRepository.save(match)).willReturn(match);
    given(matchMapper.mapToResponse(match)).willReturn(expectedResponse);

    MatchResponse actualResponse = matchService.createMatch(request);

    assertThat(actualResponse).isEqualTo(expectedResponse);
    assertThat(actualResponse.winnerRatingChange()).isEqualByComparingTo(winnerRatingGain);
    verify(matchRepository).save(match);
  }

  @ParameterizedTest(
      name =
          "[{index}] Calculate Elo-Ranking for winner rating {0}, loser rating {1}, expected change {2}")
  @CsvSource({
    "1200, 1100, 10.80",
    "1200, 1250, 17.10",
    "1300, 1200, 10.80",
    "1500, 1200, 4.50",
    "1200, 1000, 7.20"
  })
  void calculateCorrectlyPlayersEloRating_WhenPlayersAreDifferent_ShouldCalculateCorrectlyEloRating(
      String winnerRatingStr, String loserRatingStr, String expectedChangeStr) {
    BigDecimal initialWinnerRating = new BigDecimal(winnerRatingStr);
    BigDecimal initialLoserRating = new BigDecimal(loserRatingStr);
    BigDecimal expectedChange = new BigDecimal(expectedChangeStr);

    Player winner = new Player(1L, "WinnerPlayer", initialWinnerRating, Instant.now());
    Player loser = new Player(2L, "LoserPlayer", initialLoserRating, Instant.now());

    BigDecimal ratingChange = matchService.updateEloRatings(winner, loser);

    assertThat(ratingChange).isEqualByComparingTo(expectedChange);
  }
}
