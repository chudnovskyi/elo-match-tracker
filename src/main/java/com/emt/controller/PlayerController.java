package com.emt.controller;

import com.emt.model.request.CreateMatchRequest;
import com.emt.model.request.CreatePlayerRequest;
import com.emt.model.response.CreatePlayerResponse;
import com.emt.service.PlayerService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/players")
@RequiredArgsConstructor
public class PlayerController {

  private final PlayerService playerService;

  @GetMapping
  public String getAllPlayers(Model model) {
    List<CreatePlayerResponse> players = playerService.getAllPlayers();
    model.addAttribute("players", players);
    model.addAttribute("playerRequest", CreatePlayerRequest.builder().build());
    model.addAttribute("matchRequest", CreateMatchRequest.builder().build());
    return "elo-ranking";
  }

  @PostMapping("/register")
  public String createPlayer(
      @Valid @ModelAttribute("playerRequest") CreatePlayerRequest playerRequest,
      RedirectAttributes redirectAttributes) {

    playerService.createPlayer(playerRequest);
    redirectAttributes.addFlashAttribute("message", "Player added successfully!");
    return "redirect:/players";
  }
}
