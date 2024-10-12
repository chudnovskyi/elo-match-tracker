package com.emt.service;

import com.emt.entity.Match;
import com.emt.entity.Player;
import com.emt.entity.enums.MatchOutcome;
import com.emt.mapper.MatchMapper;
import com.emt.model.exception.PlayerNotFoundException;
import com.emt.model.request.CreateMatchRequest;
import com.emt.model.response.CreateMatchResponse;
import com.emt.repository.MatchRepository;
import com.emt.repository.PlayerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MatchService {

  private final PlayerRepository playerRepository;
  private final MatchRepository matchRepository;
  private final MatchMapper matchMapper;

  public List<CreateMatchResponse> getAllMatches() {
    return matchRepository.findAll().stream().map(matchMapper::mapToResponse).toList();
  }

  public CreateMatchResponse createMatch(CreateMatchRequest request, Long winnerId, Long loserId) {
    return Optional.of(request)
        .map(
            req -> {
              Player winner =
                  playerRepository
                      .findById(winnerId)
                      .orElseThrow(() -> new PlayerNotFoundException(winnerId));
              Player loser =
                  playerRepository
                      .findById(loserId)
                      .orElseThrow(() -> new PlayerNotFoundException(loserId));

              Match match = matchMapper.mapToEntity(req, winner, loser);
              match.setMatchDate(Instant.now());
              match.setOutcome(MatchOutcome.VICTORY);

              return matchRepository.save(match);
            })
        .map(matchMapper::mapToResponse)
        .orElseThrow(() -> new RuntimeException("Failed to create match"));
  }
}
