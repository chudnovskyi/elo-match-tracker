package com.emt.mapper;

import com.emt.entity.Match;
import com.emt.entity.Player;
import com.emt.model.request.CreateMatchRequest;
import com.emt.model.response.CreateMatchResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MatchMapper {

  public CreateMatchResponse mapToResponse(Match match) {
    return CreateMatchResponse.builder()
        .matchId(match.getMatchId())
        .playerOneName(match.getWinner().getUsername())
        .playerTwoName(match.getLoser().getUsername())
        .matchDate(match.getMatchDate())
        .outcome(match.getOutcome().name())
        .build();
  }

  public Match mapToEntity(CreateMatchRequest request, Player winner, Player loser) {
    return Match.builder().winner(winner).loser(loser).build();
  }
}
