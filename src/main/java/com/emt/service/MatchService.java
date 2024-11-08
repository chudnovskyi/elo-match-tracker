package com.emt.service;

import static ch.obermuhlner.math.big.BigDecimalMath.pow;
import static java.math.BigDecimal.ONE;
import static java.math.MathContext.DECIMAL128;
import static java.math.RoundingMode.HALF_UP;

import com.emt.entity.Player;
import com.emt.mapper.MatchMapper;
import com.emt.model.exception.IdenticalPlayersException;
import com.emt.model.internal.EloRatingChange;
import com.emt.model.request.CreateMatchRequest;
import com.emt.model.response.MatchResponse;
import com.emt.repository.MatchRepository;
import java.math.BigDecimal;
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

    EloRatingChange ratingChange = updateEloRatings(winner, loser);

    return Optional.of(matchMapper.mapToEntity(winner, loser, ratingChange))
        .map(matchRepository::save)
        .map(matchMapper::mapToResponse)
        .orElseThrow();
  }

  public EloRatingChange updateEloRatings(Player winner, Player loser) {
    BigDecimal probabilityWinner =
        calculateProbability(winner.getEloRating(), loser.getEloRating());
    BigDecimal winnerRatingGain = CONSTANT_K.multiply(ONE.subtract(probabilityWinner));
    BigDecimal loserRatingLoss = winnerRatingGain.negate();

    winner.setEloRating(winner.getEloRating().add(winnerRatingGain));
    loser.setEloRating(loser.getEloRating().add(loserRatingLoss));

    return new EloRatingChange(winnerRatingGain, loserRatingLoss);
  }

  public BigDecimal calculateProbability(BigDecimal rating1, BigDecimal rating2) {
    BigDecimal exponent = rating2.subtract(rating1).divide(new BigDecimal("400"), 2, HALF_UP);
    BigDecimal divisor =
        ONE.add(pow(new BigDecimal("10"), exponent, DECIMAL128).setScale(2, HALF_UP));

    return ONE.divide(divisor, 2, HALF_UP);
  }
}
