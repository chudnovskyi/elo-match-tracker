package com.emt.service;

import com.emt.entity.Player;
import com.emt.mapper.PlayerMapper;
import com.emt.model.exception.PlayerAlreadyExistsException;
import com.emt.model.exception.PlayerNotFoundException;
import com.emt.model.request.CreatePlayerRequest;
import com.emt.model.response.PlayerResponse;
import com.emt.repository.PlayerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PlayerService {

    private final PlayerRepository playerRepository;

    private final PlayerMapper playerMapper;

    public List<PlayerResponse> getAllPlayers() {
        return playerRepository.findAll().stream()
                .sorted(Comparator.comparingInt(Player::getEloRating).reversed())
                .map(playerMapper::mapToResponse)
                .toList();
    }


    public PlayerResponse createPlayer(CreatePlayerRequest request) {
        if (playerRepository.existsByNickname(request.nickname())) {
            throw new PlayerAlreadyExistsException(request.nickname());
        }

        return Optional.of(request)
                .map(playerMapper::mapToEntity)
                .map(playerRepository::save)
                .map(playerMapper::mapToResponse)
                .orElseThrow();
    }

    public Player getPlayerById(Long playerId) {
        return playerRepository.findById(playerId)
                .orElseThrow(() -> new PlayerNotFoundException(playerId));
    }
}
