package com.template.controller;

import com.template.model.request.PlayerRequest;
import com.template.service.MatchService;
import com.template.service.PlayerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/players")
public class PlayerController {

  private final PlayerService playerService;
  private final MatchService matchService;

  @GetMapping("/ranking")
  public String getEloRanking(Model model) {
    model.addAttribute("players", playerService.getAllPlayers());
    model.addAttribute("matches", matchService.getAllMatches());
    return "ranking";
  }

  @PostMapping("/register")
  public String createPlayer(@Valid @ModelAttribute PlayerRequest request) {
    playerService.createPlayer(request);
    return "redirect:/players/ranking";
  }
}
