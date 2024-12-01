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

  @ParameterizedTest(
      name =
          "[{index}] Calculate Elo-Ranking for winner rating {0}, loser rating {1}, expected change {2}")
  @CsvSource({
    "1200, 1100, 10.8",
    "1200, 1250, 17.1",
    "1300, 1200, 10.8",
    "1500, 1200, 4.5",
    "1200, 1000, 7.2"
  })
  void calculateCorrectlyPlayersEloRating_WhenPlayersAreDifferent_ShouldCalculateCorrectlyEloRating(
      String winnerRatingStr, String loserRatingStr, String expectedChangeStr) {
    BigDecimal initialWinnerRating = new BigDecimal(winnerRatingStr);
    BigDecimal initialLoserRating = new BigDecimal(loserRatingStr);
    BigDecimal expectedChange = new BigDecimal(expectedChangeStr);

    Player winner = new Player(1L, "WinnerPlayer", initialWinnerRating, Instant.now());
    Player loser = new Player(2L, "LoserPlayer", initialLoserRating, Instant.now());

    BigDecimal ratingChange = matchService.calculateRatingChange(winner.getRating(), loser.getRating());

    assertThat(ratingChange).isEqualByComparingTo(expectedChange);
  }
}
