package com.emt.service;

import com.emt.mapper.PlayerMapper;
import com.emt.model.exception.PlayerAlreadyExistsException;
import com.emt.model.request.CreatePlayerRequest;
import com.emt.model.response.CreatePlayerResponse;
import com.emt.repository.PlayerRepository;

import java.util.List;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PlayerService {

  private final PlayerRepository playerRepository;
  private final PlayerMapper playerMapper;

  public List<CreatePlayerResponse> getAllPlayers() {
    return playerRepository.findAll().stream().map(playerMapper::mapToResponse).toList();
  }

  public CreatePlayerResponse createPlayer(CreatePlayerRequest request) {
    return Optional.of(request)
        .filter(req -> !playerRepository.existsByNickname(req.nickname()))
        .map(playerMapper::mapToEntity)
        .map(playerRepository::save)
        .map(playerMapper::mapToResponse)
        .orElseThrow(() -> new PlayerAlreadyExistsException(request.nickname()));
  }
}
