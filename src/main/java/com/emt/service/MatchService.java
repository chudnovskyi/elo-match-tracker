package com.emt.service;

import com.emt.entity.Player;
import com.emt.mapper.MatchMapper;
import com.emt.model.exception.IdenticalPlayersException;
import com.emt.model.request.CreateMatchRequest;
import com.emt.model.response.MatchResponse;
import com.emt.repository.MatchRepository;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MatchService {

  private static final BigDecimal CONSTANT_K = new BigDecimal("30");
  private final MatchRepository matchRepository;
  private final MatchMapper matchMapper;
  private final PlayerService playerService;

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
    BigDecimal probabilityWinner =
        calculateProbability(winner.getEloRating(), loser.getEloRating());
    BigDecimal probabilityLoser = calculateProbability(loser.getEloRating(), winner.getEloRating());

    BigDecimal winnerNewRating =
        winner
            .getEloRating()
            .add(CONSTANT_K.multiply(BigDecimal.ONE.subtract(probabilityWinner)))
            .setScale(0, RoundingMode.HALF_UP);

    BigDecimal loserNewRating =
        loser
            .getEloRating()
            .add(CONSTANT_K.multiply(BigDecimal.ZERO.subtract(probabilityLoser)))
            .setScale(0, RoundingMode.HALF_UP);

    winner.setEloRating(winnerNewRating);
    loser.setEloRating(loserNewRating);
  }

  public BigDecimal calculateProbability(BigDecimal rating1, BigDecimal rating2) {
    double exponent =
        rating2
            .subtract(rating1)
            .divide(new BigDecimal("400"), MathContext.DECIMAL128)
            .doubleValue();
    BigDecimal divisor = BigDecimal.ONE.add(new BigDecimal(Math.pow(10, exponent)));

    return BigDecimal.ONE.divide(divisor, 10, RoundingMode.HALF_UP);
  }
}
