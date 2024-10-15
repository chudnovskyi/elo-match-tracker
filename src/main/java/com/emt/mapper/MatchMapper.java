package com.emt.mapper;

import com.emt.entity.Match;
import com.emt.entity.Player;
import com.emt.model.response.CreateMatchResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
@RequiredArgsConstructor
public class MatchMapper {

  public Match mapToEntity(Player playerOneName, Player playerTwoName, String winner) {
    return Match.builder()
        .playerOne(playerOneName)
        .playerTwo(playerTwoName)
        .winner(winner)
        .matchTime(Instant.now())
        .build();
  }

  public CreateMatchResponse mapToResponse(Match match) {
    return CreateMatchResponse.builder()
        .matchId(match.getMatchId())
        .playerOneName(match.getPlayerOne())
        .playerTwoName(match.getPlayerTwo())
        .winner(match.getWinner())
        .matchTime(match.getMatchTime())
        .build();
  }
}
