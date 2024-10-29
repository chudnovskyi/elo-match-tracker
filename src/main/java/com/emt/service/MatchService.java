package com.emt.service;

import com.emt.entity.Player;
import com.emt.mapper.MatchMapper;
import com.emt.model.exception.IdenticalPlayersException;
import com.emt.model.request.CreateMatchRequest;
import com.emt.model.response.MatchResponse;
import com.emt.repository.MatchRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MatchService {

  private static final int CONSTANT_K = 30;
  private final MatchRepository matchRepository;
  private final MatchMapper matchMapper;
  private final PlayerService playerService;

  public static double calculateProbability(int rating1, int rating2) {
    return 1.0 / (1 + Math.pow(10, (rating1 - rating2) / 400.0));
  }

  public List<MatchResponse> getAllMatches() {
    return matchRepository.findAll().stream().map(matchMapper::mapToResponse).toList();
  }

  public MatchResponse createMatch(CreateMatchRequest request) {
    if (request.winnerId().equals(request.loserId())) {
      throw new IdenticalPlayersException("A match cannot be created with identical players.");
    }

    Player winner = playerService.getPlayerById(request.winnerId());
    Player loser = playerService.getPlayerById(request.loserId());

    updateEloRatings(winner, loser);

    return Optional.of(matchMapper.mapToEntity(winner, loser))
        .map(matchRepository::save)
        .map(matchMapper::mapToResponse)
        .orElseThrow();
  }

  public void updateEloRatings(Player winner, Player loser) {
    double probabilityLoser = calculateProbability(winner.getEloRating(), loser.getEloRating());
    double probabilityWinner = calculateProbability(loser.getEloRating(), winner.getEloRating());

    winner.setEloRating((int) (winner.getEloRating() + CONSTANT_K * (1.0 - probabilityWinner)));
    loser.setEloRating((int) (loser.getEloRating() + CONSTANT_K * (0.0 - probabilityLoser)));
  }
}
