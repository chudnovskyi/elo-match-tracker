package com.template.mapper;

import com.template.entity.Match;
import com.template.entity.Player;
import com.template.model.request.MatchRequest;
import com.template.model.response.MatchResponse;
import com.template.repository.PlayerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class MatchMapper {

    private final PlayerRepository playerRepository;

    public MatchResponse mapToResponse(Match match) {
        String outcome = (match.getPlayer1Score() > match.getPlayer2Score())
                ? "Player 1 Wins"
                : (match.getPlayer1Score() < match.getPlayer2Score())
                ? "Player 2 Wins"
                : "Draw";

        return new MatchResponse(
                match.getMatchId(),
                match.getPlayer1().getUsername(),
                match.getPlayer2().getUsername(),
                match.getPlayer1Score(),
                match.getPlayer2Score(),
                match.getMatchDate(),
                outcome
        );
    }

    public Match mapToEntity(MatchRequest request) {
        Player player1 = playerRepository.findById(request.player1Id())
                .orElseThrow(() -> new IllegalArgumentException("Invalid Player 1 ID"));
        Player player2 = playerRepository.findById(request.player2Id())
                .orElseThrow(() -> new IllegalArgumentException("Invalid Player 2 ID"));

        return Match.builder()
                .player1(player1)
                .player2(player2)
                .player1Score(request.player1Score())
                .player2Score(request.player2Score())
                .matchDate(LocalDateTime.now())
                .build();
    }
}
