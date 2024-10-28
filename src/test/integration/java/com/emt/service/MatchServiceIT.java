package com.emt.service;

import com.emt.ITBase;
import com.emt.entity.Player;
import com.emt.model.request.CreateMatchRequest;
import com.emt.model.request.CreatePlayerRequest;
import com.emt.model.response.MatchResponse;
import com.emt.model.response.PlayerResponse;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@RequiredArgsConstructor
public class MatchServiceIT extends ITBase {

    private final MatchService matchService;

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

        int initialRatingFirstPlayer = firstPlayer.eloRating();
        int initialRatingSecondPlayer = secondPlayer.eloRating();

        matchService.createMatch(new CreateMatchRequest(firstPlayer.playerId(), secondPlayer.playerId()));

        Player updatedFirstPlayer = playerService.getPlayerById(firstPlayer.playerId());
        Player updatedSecondPlayer = playerService.getPlayerById(secondPlayer.playerId());

        assertThat(updatedFirstPlayer.getEloRating()).isGreaterThan(initialRatingFirstPlayer);
        assertThat(updatedSecondPlayer.getEloRating()).isLessThan(initialRatingSecondPlayer);
    }
}
