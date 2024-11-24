package com.emt.service;

import static java.math.BigDecimal.ZERO;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.emt.ITBase;
import com.emt.entity.Match;
import com.emt.entity.Player;
import com.emt.model.exception.MatchNotFoundException;
import com.emt.model.request.CreateMatchRequest;
import com.emt.model.request.CreatePlayerRequest;
import com.emt.model.response.MatchResponse;
import com.emt.model.response.PlayerResponse;
import com.emt.repository.MatchRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Stream;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;

@RequiredArgsConstructor
public class MatchServiceIT extends ITBase {

  private final MatchService matchService;
  private final MatchRepository matchRepository;
  private final PlayerService playerService;

  @Test
  public void testGetAllMatches_WhenMatchIsCreatedSuccessfully_ShouldReturnAllMatches() {
    PlayerResponse firstPlayer =
        playerService.createPlayer(CreatePlayerRequest.builder().nickname("Winner").build());
    PlayerResponse secondPlayer =
        playerService.createPlayer(CreatePlayerRequest.builder().nickname("Loser").build());

    CreateMatchRequest matchRequest =
        new CreateMatchRequest(firstPlayer.playerId(), secondPlayer.playerId());
    matchService.createMatch(matchRequest);

    List<MatchResponse> matches = matchService.getAllMatches();

    assertEquals(1, matches.size());
    assertEquals("Winner", matches.get(0).winnerName());
    assertEquals("Loser", matches.get(0).loserName());
  }

  @Test
  public void testEloRatingUpdateAfterMatch() {
    PlayerResponse firstPlayer =
        playerService.createPlayer(CreatePlayerRequest.builder().nickname("Winner").build());
    PlayerResponse secondPlayer =
        playerService.createPlayer(CreatePlayerRequest.builder().nickname("Loser").build());

    BigDecimal initialRatingFirstPlayer = firstPlayer.eloRating();
    BigDecimal initialRatingSecondPlayer = secondPlayer.eloRating();

    matchService.createMatch(
        new CreateMatchRequest(firstPlayer.playerId(), secondPlayer.playerId()));

    Player updatedFirstPlayer = playerService.getPlayerById(firstPlayer.playerId());
    Player updatedSecondPlayer = playerService.getPlayerById(secondPlayer.playerId());

    assertThat(updatedFirstPlayer.getEloRating()).isGreaterThan(initialRatingFirstPlayer);
    assertThat(updatedSecondPlayer.getEloRating()).isLessThan(initialRatingSecondPlayer);
  }

  @Test
  public void testCancelNonExistentMatch_shouldThrowMatchNotFoundException() {
    Long nonExistentMatchId = 999L;
    MatchNotFoundException exception =
        assertThrows(
            MatchNotFoundException.class, () -> matchService.cancelMatch(nonExistentMatchId));

    assertThat(exception.getMessage()).contains("Match not found with id");
  }

  @Test
  public void testCancelMatch_shouldHandleEloReversionAndMatchRemovalInComplexScenario() {
    PlayerResponse playerOne =
        playerService.createPlayer(CreatePlayerRequest.builder().nickname("PlayerOne").build());
    PlayerResponse playerTwo =
        playerService.createPlayer(CreatePlayerRequest.builder().nickname("PlayerTwo").build());
    PlayerResponse playerThree =
        playerService.createPlayer(CreatePlayerRequest.builder().nickname("PlayerThree").build());
    PlayerResponse playerFour =
        playerService.createPlayer(CreatePlayerRequest.builder().nickname("PlayerFour").build());

    MatchResponse firstMatch =
        matchService.createMatch(
            new CreateMatchRequest(playerOne.playerId(), playerTwo.playerId()));
    MatchResponse secondMatch =
        matchService.createMatch(
            new CreateMatchRequest(playerTwo.playerId(), playerThree.playerId()));
    MatchResponse thirdMatch_0 =
        matchService.createMatch(
            new CreateMatchRequest(playerThree.playerId(), playerFour.playerId()));
    MatchResponse thirdMatch_1 =
        matchService.createMatch(
            new CreateMatchRequest(playerThree.playerId(), playerFour.playerId()));
    MatchResponse thirdMatch_2 =
        matchService.createMatch(
            new CreateMatchRequest(playerThree.playerId(), playerFour.playerId()));
    MatchResponse thirdMatch_3 =
        matchService.createMatch(
            new CreateMatchRequest(playerThree.playerId(), playerFour.playerId()));

    matchService.cancelMatch(secondMatch.matchId());

    Player updatedPlayerOne = playerService.getPlayerById(playerOne.playerId());
    Player updatedPlayerTwo = playerService.getPlayerById(playerTwo.playerId());
    Player updatedPlayerThree = playerService.getPlayerById(playerThree.playerId());
    Player updatedPlayerFour = playerService.getPlayerById(playerFour.playerId());

    // hardcoding answers is a bad practice, you can't really tell, if these are actually correct. In this case, they are not.
    //    assertThat(updatedPlayerOne.getEloRating()).isEqualTo(new BigDecimal("1215.00"));
    //    assertThat(updatedPlayerTwo.getEloRating()).isEqualTo(new BigDecimal("1185.00"));
    //    assertThat(updatedPlayerThree.getEloRating()).isEqualTo(new BigDecimal("1247.70"));
    //    assertThat(updatedPlayerFour.getEloRating()).isEqualTo(new BigDecimal("1152.30"));
    assertThat(updatedPlayerOne.getEloRating()).isEqualTo(new BigDecimal("1215.00"));
    assertThat(updatedPlayerTwo.getEloRating()).isEqualTo(new BigDecimal("1185.00"));
    assertThat(updatedPlayerThree.getEloRating()).isEqualTo(new BigDecimal("1252.5"));
    assertThat(updatedPlayerFour.getEloRating()).isEqualTo(new BigDecimal("1147.5"));

    /* should be:
       0 = winnerRatingChange=15.00
       1 = winnerRatingChange=13.50
       2 = winnerRatingChange=12.60
       3 = winnerRatingChange=11.40 */
    List<Match> updatedMatches = matchRepository.findAllById(Stream.of(thirdMatch_0, thirdMatch_1, thirdMatch_2, thirdMatch_3).map(MatchResponse::matchId).toList());
    BigDecimal actualRatingChange = new BigDecimal("1200").subtract(updatedPlayerFour.getEloRating());
    BigDecimal expectedRatingChange = updatedMatches.stream().map(Match::getWinnerRatingChange).reduce(ZERO, BigDecimal::add);
    assertThat(actualRatingChange)
        .isEqualTo(expectedRatingChange)
        .isEqualTo(new BigDecimal("15.00").add(new BigDecimal("13.50").add(new BigDecimal("12.60").add(new BigDecimal("11.40")))));

    assertThat(matchRepository.existsById(secondMatch.matchId())).isFalse();
    assertThat(matchRepository.existsById(firstMatch.matchId())).isTrue();
    assertThat(matchRepository.existsById(thirdMatch_0.matchId())).isTrue();
  }
}
