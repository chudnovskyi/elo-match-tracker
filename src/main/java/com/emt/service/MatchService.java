package com.emt.service;

import com.emt.entity.Player;
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

    private static final int CONSTANT_K = 30;

    public List<MatchResponse> getAllMatches() {
        return matchRepository.findAll().stream().map(matchMapper::mapToResponse).toList();
    }

    public MatchResponse createMatch(CreateMatchRequest request) {
        if (request.winnerId().equals(request.loserId())) {
            throw new IdenticalPlayersException("A match cannot be created with identical players.");
        }

        return Optional.of(request)
                .map(req -> List.of(
                        playerService.getReferenceById(req.winnerId()),
                        playerService.getReferenceById(req.loserId())))
                .map(players -> {
                    updateEloRatings(players.get(0), players.get(1), 1.0);
                    return matchMapper.mapToEntity(players.get(0), players.get(1));
                })
                .map(matchRepository::save)
                .map(matchMapper::mapToResponse)
                .orElseThrow();
    }

    public static double calculateProbability(int rating1, int rating2) {
        return 1.0 / (1 + Math.pow(10, (rating1 - rating2) / 400.0));
    }

    public void updateEloRatings(Player winner, Player loser, double outcome) {
        double probabilityLoser = calculateProbability(winner.getEloRating(), loser.getEloRating());
        double probabilityWinner = calculateProbability(loser.getEloRating(), winner.getEloRating());

        winner.setEloRating((int) (winner.getEloRating() + CONSTANT_K * (outcome - probabilityWinner)));
        loser.setEloRating((int) (loser.getEloRating() + CONSTANT_K * ((1 - outcome) - probabilityLoser)));
    }
}
