package com.emt.service;

import com.emt.ITBase;
import com.emt.entity.Player;
import com.emt.mapper.PlayerMapper;
import com.emt.model.request.CreatePlayerRequest;
import com.emt.model.response.PlayerResponse;
import com.emt.repository.PlayerRepository;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RequiredArgsConstructor
public class PlayerServiceIT extends ITBase {

    private final PlayerRepository playerRepository;

    private final PlayerService playerService;

    private final PlayerMapper playerMapper;

    private static final String NICKNAME = "top4ik";

    @BeforeEach
    public void setUp() {
        playerRepository.deleteAll();
        playerRepository.save(new Player(1L, "Player1", 1500, Instant.now()));
        playerRepository.save(new Player(2L, "Player2", 1800, Instant.now()));
        playerRepository.save(new Player(3L, "Player3", 1700, Instant.now()));
    }

    @Test
    public void testGetAllPlayers_SortedByEloRating() {
        List<PlayerResponse> players = playerService.getAllPlayers();

        assertThat(players).hasSize(3);
        assertThat(players.get(0).nickname()).isEqualTo("Player2");
        assertThat(players.get(1).nickname()).isEqualTo("Player3");
        assertThat(players.get(2).nickname()).isEqualTo("Player1");

        assertThat(players.get(0).eloRating()).isEqualTo(1800);
        assertThat(players.get(1).eloRating()).isEqualTo(1700);
        assertThat(players.get(2).eloRating()).isEqualTo(1500);
    }

    @Test
    public void testCreatePlayer_Success() {
        PlayerResponse actualPlayer =
                playerService.createPlayer(CreatePlayerRequest.builder().nickname(NICKNAME).build());

        assertThat(actualPlayer).isNotNull();
        assertThat(actualPlayer.nickname()).isEqualTo(NICKNAME);

        Player savedPlayer = playerRepository.findById(actualPlayer.playerId()).orElseThrow();

        Player expectedPlayer =
                new Player(savedPlayer.getPlayerId(), NICKNAME, 1200, savedPlayer.getRegisteredAt());
        PlayerResponse expectedResponse = playerMapper.mapToResponse(expectedPlayer);

        assertThat(savedPlayer.getNickname()).isEqualTo(expectedPlayer.getNickname());
        assertThat(actualPlayer).isEqualTo(expectedResponse);
    }
}
