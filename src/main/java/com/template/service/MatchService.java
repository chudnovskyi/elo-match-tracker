package com.template.service;

import com.template.entity.Match;
import com.template.mapper.MatchMapper;
import com.template.model.exception.MatchAlreadyExistsException;
import com.template.model.exception.MatchNotFoundException;
import com.template.model.request.MatchRequest;
import com.template.model.response.MatchResponse;
import com.template.repository.MatchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MatchService {

    private final MatchRepository matchRepository;
    private final MatchMapper matchMapper;

    public List<MatchResponse> getAllMatches() {
        return matchRepository.findAll()
                .stream()
                .map(matchMapper::mapToResponse)
                .collect(Collectors.toList());
    }

    public MatchResponse createMatch(MatchRequest request) {
        Match match = matchMapper.mapToEntity(request);
        return Optional.of(match)
                .map(matchRepository::save)
                .map(matchMapper::mapToResponse)
                .orElseThrow(() -> new MatchAlreadyExistsException("Failed to create match."));
    }
}
