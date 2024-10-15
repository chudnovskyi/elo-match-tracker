package com.emt.mapper;

import com.emt.entity.Player;
import com.emt.model.request.CreatePlayerRequest;
import com.emt.model.response.CreatePlayerResponse;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class PlayerMapper {

  public Player mapToEntity(CreatePlayerRequest request) {
    return Player.builder()
        .nickname(request.nickname())
        .eloRating(1200)
        .registeredAt(Instant.now())
        .build();
  }

  public CreatePlayerResponse mapToResponse(Player player) {
    return CreatePlayerResponse.builder()
        .playerId(player.getPlayerId())
        .nickname(player.getNickname())
        .eloRating(player.getEloRating())
        .registeredAt(player.getRegisteredAt())
        .build();
  }
}
