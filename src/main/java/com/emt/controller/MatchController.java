package com.emt.controller;

import com.emt.model.exception.PlayerNotFoundException;
import com.emt.model.request.CreateMatchRequest;
import com.emt.model.response.CreateMatchResponse;
import com.emt.service.MatchService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/matches")
@RequiredArgsConstructor
public class MatchController {

  private final MatchService matchService;

  @PostMapping("/report")
  public String reportMatch(
      @Valid @ModelAttribute CreateMatchRequest matchRequest,
      @RequestParam Long winnerId,
      @RequestParam Long loserId,
      RedirectAttributes redirectAttributes) {
    try {
      matchService.createMatch(matchRequest, winnerId, loserId);
      redirectAttributes.addFlashAttribute("message", "Match reported successfully!");
    } catch (PlayerNotFoundException e) {
      redirectAttributes.addFlashAttribute("error", "One of the players was not found.");
    } catch (Exception e) {
      redirectAttributes.addFlashAttribute("error", "An error occurred while reporting the match.");
    }
    return "redirect:/matches/elo-ranking";
  }

  @GetMapping("/history")
  public String getMatchHistory(Model model) {
    List<CreateMatchResponse> matches = matchService.getAllMatches();
    model.addAttribute("matches", matches);
    return "match-history";
  }
}
