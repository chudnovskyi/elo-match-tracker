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

  public Match mapToEntity(Player winner, Player looser) {
    return Match.builder().winner(winner).looser(looser).createdAt(Instant.now()).build();
  }

  public MatchResponse mapToResponse(Match match) {
    return MatchResponse.builder()
        .winnerName(match.getWinner().getNickname())
        .looserName(match.getLooser().getNickname())
        .createdAt(match.getCreatedAt())
        .build();
  }
}
