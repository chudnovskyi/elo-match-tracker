package com.template.mapper;

import com.template.entity.Player;
import com.template.model.request.PlayerRequest;
import com.template.model.response.PlayerResponse;
import org.springframework.stereotype.Component;

@Component
public class PlayerMapper {

  public PlayerResponse mapToResponse(Player player) {
    return new PlayerResponse(player.getPlayerId(), player.getUsername(), player.getEloRating());
  }

  public Player mapToEntity(PlayerRequest request) {
    return Player.builder().username(request.username()).eloRating(1000).build();
  }
}
