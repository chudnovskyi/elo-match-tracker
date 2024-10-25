package com.emt.mapper;

import com.emt.entity.Match;
import com.emt.entity.Player;
import com.emt.model.response.MatchResponse;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MatchMapper {

  public Match mapToEntity(Player winner, Player loser) {
    return Match.builder().winner(winner).loser(loser).createdAt(Instant.now()).build();
  }

  public MatchResponse mapToResponse(Match match) {
    return MatchResponse.builder()
        .winnerName(match.getWinner().getNickname())
        .loserName(match.getLoser().getNickname())
        .createdAt(match.getCreatedAt())
        .build();
  }
}
