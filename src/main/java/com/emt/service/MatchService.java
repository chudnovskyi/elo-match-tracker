package com.emt.service;

import com.emt.mapper.MatchMapper;
import com.emt.model.exception.IdenticalPlayersException;
import com.emt.model.request.CreateMatchRequest;
import com.emt.model.response.CreateMatchResponse;
import com.emt.repository.MatchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MatchService {

  private final MatchRepository matchRepository;
  private final MatchMapper matchMapper;

  public List<CreateMatchResponse> getAllMatches() {
    return matchRepository.findAll().stream().map(matchMapper::mapToResponse).toList();
  }

  public CreateMatchResponse createMatch(CreateMatchRequest request) {
    return Optional.of(request)
        .filter(req -> !req.playerOneName().equals(req.playerTwoName()))
        .map(req -> matchMapper.mapToEntity(req.playerOneName(), req.playerTwoName(), req.winner()))
        .map(matchRepository::save)
        .map(matchMapper::mapToResponse)
        .orElseThrow(
            () ->
                new IdenticalPlayersException("A match cannot be created with identical players."));
  }
}
