package com.emt.service;

import static ch.obermuhlner.math.big.BigDecimalMath.pow;
import static java.math.BigDecimal.ONE;
import static java.math.MathContext.DECIMAL128;
import static java.math.RoundingMode.HALF_UP;

import com.emt.entity.Match;
import com.emt.entity.Player;
import com.emt.mapper.MatchMapper;
import com.emt.model.exception.IdenticalPlayersException;
import com.emt.model.exception.MatchNotFoundException;
import com.emt.model.request.CreateMatchRequest;
import com.emt.model.response.MatchResponse;
import com.emt.repository.MatchRepository;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

  public Match getMatchById(Long matchId) {
    return matchRepository.findById(matchId).orElseThrow(() -> new MatchNotFoundException(matchId));
  }

  public MatchResponse createMatch(CreateMatchRequest request) {
    if (request.winnerId().equals(request.loserId())) {
      throw new IdenticalPlayersException();
    }

    Player winner = playerService.getPlayerById(request.winnerId());
    Player loser = playerService.getPlayerById(request.loserId());

    BigDecimal ratingChange = calculateRatingChange(winner.getRating(), loser.getRating());
    winner.setRating(winner.getRating().add(ratingChange));
    loser.setRating(loser.getRating().subtract(ratingChange));

    return Optional.of(matchMapper.mapToEntity(winner, loser, ratingChange))
        .map(matchRepository::save)
        .map(matchMapper::mapToResponse)
        .orElseThrow();
  }

  @Transactional
  public void cancelMatch(Long matchId) {
    Map<Long, BigDecimal> playerIdToRatingMap = new HashMap<>();

    Match match = getMatchById(matchId);

    Long winnerId = match.getWinner().getPlayerId();
    Long loserId = match.getLoser().getPlayerId();

    BigDecimal ratingChange = match.getRatingChange();
    playerIdToRatingMap.put(winnerId, match.getWinnerRating().subtract(ratingChange));
    playerIdToRatingMap.put(loserId, match.getLoserRating().add(ratingChange));

    List<Match> subsequentMatches =
        matchRepository.findMatchesByPlayersAfter(match.getCreatedAt(), winnerId, loserId);

    recalculateEloRatingsForSubsequentMatches(subsequentMatches, playerIdToRatingMap);

    matchRepository.deleteById(matchId);

    playerIdToRatingMap.forEach(playerService::updatePlayerRating);
  }

  public void recalculateEloRatingsForSubsequentMatches(List<Match> matches, Map<Long, BigDecimal> playerIdToRatingMap) {
    for (Match match : matches) {
      Long winnerId = match.getWinner().getPlayerId();
      Long loserId = match.getLoser().getPlayerId();
      BigDecimal winnerRating = Optional.ofNullable(playerIdToRatingMap.get(winnerId)).orElseGet(() -> match.getWinnerRating().subtract(match.getRatingChange()));
      BigDecimal loserRating = Optional.ofNullable(playerIdToRatingMap.get(loserId)).orElseGet(() -> match.getLoserRating().add(match.getRatingChange()));

      BigDecimal newRatingChange = calculateRatingChange(winnerRating, loserRating);
      BigDecimal newWinnerRating = winnerRating.add(newRatingChange);
      BigDecimal newLoserRating = loserRating.subtract(newRatingChange);

      match.setWinnerRating(newWinnerRating);
      match.setLoserRating(newLoserRating);
      match.setRatingChange(newRatingChange);

      playerIdToRatingMap.put(winnerId, newWinnerRating);
      playerIdToRatingMap.put(loserId, newLoserRating);

      matchRepository.save(match);
    }
  }

  public BigDecimal calculateRatingChange(BigDecimal rating1, BigDecimal rating2) {
    BigDecimal exponent = rating2.subtract(rating1).divide(new BigDecimal("400"), 2, HALF_UP);
    BigDecimal divisor = ONE.add(pow(new BigDecimal("10"), exponent, DECIMAL128).setScale(2, HALF_UP));
    BigDecimal probabilityWinner = ONE.divide(divisor, 2, HALF_UP);
    return CONSTANT_K.multiply(ONE.subtract(probabilityWinner));
  }
}
