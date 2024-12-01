package com.emt.mapper;

import com.emt.entity.Player;
import com.emt.model.request.CreatePlayerRequest;
import com.emt.model.response.PlayerResponse;
import java.time.Instant;
import org.springframework.stereotype.Component;

@Component
public class PlayerMapper {

  public Player mapToEntity(CreatePlayerRequest request) {
    return Player.builder()
        .nickname(request.nickname())
        .registeredAt(Instant.now())
        .build();
  }

  public PlayerResponse mapToResponse(Player player) {
    return PlayerResponse.builder()
        .playerId(player.getPlayerId())
        .nickname(player.getNickname())
        .eloRating(player.getRating())
        .registeredAt(player.getRegisteredAt())
        .build();
  }
}
