package com.emt.service;

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

    return Optional.of(request)
        .map(
            req ->
                matchMapper.mapToEntity(
                    playerService.getReferenceById(req.winnerId()),
                    playerService.getReferenceById(req.loserId())))
        .map(matchRepository::save)
        .map(matchMapper::mapToResponse)
        .orElseThrow();
  }
}
