package com.emt.mapper;

import com.emt.entity.Match;
import com.emt.entity.Player;
import com.emt.model.response.MatchResponse;
import java.math.BigDecimal;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MatchMapper {

  public Match mapToEntity(Player winner, Player loser, BigDecimal winnerRatingChange) {
    return Match.builder()
        .winner(winner)
        .loser(loser)
        .winnerRatingChange(winnerRatingChange)
        .createdAt(Instant.now())
        .build();
  }

  public MatchResponse mapToResponse(Match match) {
    return MatchResponse.builder()
        .matchId(match.getMatchId())
        .winnerName(match.getWinner().getNickname())
        .loserName(match.getLoser().getNickname())
        .winnerRatingChange(match.getWinnerRatingChange())
        .createdAt(match.getCreatedAt())
        .build();
  }
}
