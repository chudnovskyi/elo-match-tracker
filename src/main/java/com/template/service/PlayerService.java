package com.template.service;

import com.template.mapper.PlayerMapper;
import com.template.model.exception.PlayerAlreadyExistsException;
import com.template.model.exception.PlayerNotFoundException;
import com.template.model.request.PlayerRequest;
import com.template.model.response.PlayerResponse;
import com.template.repository.PlayerRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PlayerService {

  private final PlayerRepository playerRepository;
  private final PlayerMapper playerMapper;

  public PlayerResponse getPlayerById(Long playerId) {
    return playerRepository
        .findById(playerId)
        .map(playerMapper::mapToResponse)
        .orElseThrow(() -> new PlayerNotFoundException(playerId));
  }

  public PlayerResponse createPlayer(PlayerRequest request) {
    return Optional.of(request)
        .map(playerMapper::mapToEntity)
        .map(playerRepository::save)
        .map(playerMapper::mapToResponse)
        .orElseThrow(() -> new PlayerAlreadyExistsException("Failed."));
  }
}
