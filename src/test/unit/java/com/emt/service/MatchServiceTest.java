package com.emt.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.within;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.emt.entity.Match;
import com.emt.entity.Player;
import com.emt.mapper.MatchMapper;
import com.emt.model.exception.IdenticalPlayersException;
import com.emt.model.request.CreateMatchRequest;
import com.emt.model.response.MatchResponse;
import com.emt.repository.MatchRepository;
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

  private static final int CONSTANT_K = 30;

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

    given(playerService.getPlayerById(1L)).willReturn(winner);
    given(playerService.getPlayerById(2L)).willReturn(loser);
    given(matchMapper.mapToEntity(winner, loser)).willReturn(match);
    given(matchRepository.save(match)).willReturn(match);
    given(matchMapper.mapToResponse(match)).willReturn(expectedResponse);

    MatchResponse actualResponse = matchService.createMatch(request);

    assertThat(actualResponse).isEqualTo(expectedResponse);
    verify(matchRepository).save(match);
  }

  @ParameterizedTest(
      name =
          "[{index}] Calculate correctly Elo-Ranking between two players with winner rating {0} and loser rating {1}, expected rating change {2}.")
  @CsvSource({
    "1200, 1100, 11",
    "1200, 1250, 17",
    "1300, 1200, 11",
    "1500, 1200, 5",
    "1200, 1000, 7"
  })
  void calculateCorrectlyPlayersEloRating_WhenPlayersAreDifferent_ShouldCalculateCorrectlyEloRating(
      int winnerRating, int loserRating, int ratingChange) {
    Player winner = new Player(1L, "WinnerPlayer", winnerRating, Instant.now());
    Player loser = new Player(2L, "LoserPlayer", loserRating, Instant.now());

    matchService.updateEloRatings(winner, loser);

    double probabilityWinner = 1.0 / (1.0 + Math.pow(10, (loserRating - winnerRating) / 400.0));
    double probabilityLoser = 1.0 / (1.0 + Math.pow(10, (winnerRating - loserRating) / 400.0));

    int expectedWinnerRating =
        (int) Math.round(winnerRating + CONSTANT_K * (1.0 - probabilityWinner));
    int expectedLoserRating = (int) Math.round(loserRating + CONSTANT_K * (0.0 - probabilityLoser));
    int calculatedRatingChange = expectedWinnerRating - winnerRating;

    assertThat(winner.getEloRating()).isCloseTo(expectedWinnerRating, within(1));
    assertThat(loser.getEloRating()).isCloseTo(expectedLoserRating, within(1));

    assertThat(calculatedRatingChange).isCloseTo(ratingChange, within(1));
  }
}
