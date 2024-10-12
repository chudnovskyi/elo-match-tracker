package com.emt.controller;

import com.emt.model.exception.PlayerAlreadyExistsException;
import com.emt.model.request.CreatePlayerRequest;
import com.emt.model.response.CreatePlayerResponse;
import com.emt.service.PlayerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/players")
@RequiredArgsConstructor
public class PlayerController {

  private final PlayerService playerService;

  @GetMapping
  public String getAllPlayers(Model model) {
    List<CreatePlayerResponse> players = playerService.getAllPlayers();
    model.addAttribute("players", players);
    return "elo-ranking";
  }

  @PostMapping("/register")
  public String createPlayer(
      @Valid @ModelAttribute CreatePlayerRequest playerRequest,
      RedirectAttributes redirectAttributes) {
    try {
      playerService.createPlayer(playerRequest);
      redirectAttributes.addFlashAttribute("message", "Player added successfully!");
    } catch (PlayerAlreadyExistsException e) {
      redirectAttributes.addFlashAttribute("error", e.getMessage());
    }
    return "redirect:/players";
  }
}
