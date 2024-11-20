package com.emt.service;

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
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
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
  @Transactional
  public void testCancelMatch_shouldRevertEloRatingsAndRemoveMatch() {
    PlayerResponse winner =
        playerService.createPlayer(CreatePlayerRequest.builder().nickname("Winner").build());
    PlayerResponse loser =
        playerService.createPlayer(CreatePlayerRequest.builder().nickname("Loser").build());

    BigDecimal initialRatingWinner = winner.eloRating();

    CreateMatchRequest matchRequest = new CreateMatchRequest(winner.playerId(), loser.playerId());
    MatchResponse matchToCancel = matchService.createMatch(matchRequest);

    matchService.cancelMatch(matchToCancel.matchId());

    Player updatedWinner = playerService.getPlayerById(winner.playerId());

    assertThat(updatedWinner.getEloRating().setScale(2, BigDecimal.ROUND_HALF_UP))
        .isEqualTo(initialRatingWinner.setScale(2, BigDecimal.ROUND_HALF_UP));
    assertThat(matchRepository.count()).isZero();
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
  @Transactional
  public void testCancelMatch_shouldRevertEloRatingsAndRemoveOneMatch() {
    PlayerResponse[] players = new PlayerResponse[6];
    for (int i = 0; i < players.length; i++) {
      players[i] =
          playerService.createPlayer(
              CreatePlayerRequest.builder().nickname("Player" + (i + 1)).build());
    }

    BigDecimal[] initialRatings = new BigDecimal[players.length];
    for (int i = 0; i < players.length; i++) {
      initialRatings[i] = players[i].eloRating();
    }

    List<MatchResponse> createdMatches = new ArrayList<>();
    for (int i = 0; i < players.length; i += 2) {
      CreateMatchRequest matchRequest =
          new CreateMatchRequest(players[i].playerId(), players[i + 1].playerId());
      createdMatches.add(matchService.createMatch(matchRequest));
    }

    matchService.cancelMatch(createdMatches.get(1).matchId());

    List<Match> subsequentMatches =
        matchRepository.findMatchesByPlayersAfter(
            createdMatches.get(1).createdAt(), players[2].playerId(), players[3].playerId());

    for (Match match : subsequentMatches) {
      if (match.getWinner().getPlayerId() == players[3].playerId()
          || match.getLoser().getPlayerId() == players[3].playerId()) {
        Player updatedPlayer = playerService.getPlayerById(players[3].playerId());
        assertThat(updatedPlayer.getEloRating())
            .isEqualTo(initialRatings[3].setScale(2, BigDecimal.ROUND_HALF_UP));
      }
    }
  }
}
