package com.template.controller;

import static org.springframework.http.HttpStatus.OK;

import com.template.model.request.PlayerRequest;
import com.template.model.response.PlayerResponse;
import com.template.service.PlayerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/players")
public class PlayerController {

  private final PlayerService playerService;

  @GetMapping("/")
  public ResponseEntity<PlayerResponse> getPlayerById(@RequestParam Long playerId) {
    return new ResponseEntity<>(playerService.getPlayerById(playerId), OK);
  }

  @PostMapping("/")
  public ResponseEntity<PlayerResponse> createPlayer(@Valid @RequestBody PlayerRequest request) {
    return new ResponseEntity<>(playerService.createPlayer(request), OK);
  }
}