package com.emt.model.exception;

public class MatchNotFoundException extends RuntimeException {
    public MatchNotFoundException(Long matchId) {
        super("Match not found with id %s".formatted(matchId));
    }
}
