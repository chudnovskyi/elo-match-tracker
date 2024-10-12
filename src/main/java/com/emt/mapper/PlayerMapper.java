package com.emt.mapper;

import com.emt.entity.Player;
import com.emt.model.request.CreatePlayerRequest;
import com.emt.model.response.CreatePlayerResponse;
import org.springframework.stereotype.Component;

@Component
public class PlayerMapper {

  public CreatePlayerResponse mapToResponse(Player player) {
    return CreatePlayerResponse.builder()
        .playerId(player.getPlayerId())
        .username(player.getUsername())
        .eloRating(player.getEloRating())
        .registeredAt(player.getRegisteredAt())
        .build();
  }

  public Player mapToEntity(CreatePlayerRequest request) {
    return Player.builder().username(request.username()).build();
  }
}
