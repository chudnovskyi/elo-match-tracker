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

  public Match mapToEntity(Player winner, Player loser, BigDecimal ratingChange) {
    return Match.builder()
        .winner(winner)
        .winnerRating(winner.getRating())
        .loser(loser)
        .loserRating(loser.getRating())
        .ratingChange(ratingChange)
        .createdAt(Instant.now())
        .build();
  }

  public MatchResponse mapToResponse(Match match) {
    return MatchResponse.builder()
        .matchId(match.getMatchId())
        .winnerName(match.getWinner().getNickname())
        .loserName(match.getLoser().getNickname())
        .ratingChange(match.getRatingChange())
        .winnerRating(match.getWinnerRating())
        .loserRating(match.getLoserRating())
        .createdAt(match.getCreatedAt())
        .build();
  }
}
