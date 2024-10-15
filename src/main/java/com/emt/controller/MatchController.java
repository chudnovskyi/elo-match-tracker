package com.emt.controller;

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

  @GetMapping
  public String getAllMatches(Model model) {
    List<CreateMatchResponse> matches = matchService.getAllMatches();
    model.addAttribute("matches", matches);
    model.addAttribute("matchRequest", new CreateMatchRequest(null, null, null));
    return "elo-ranking";
  }

  @GetMapping("/history")
  public String showMatchHistory(Model model) {
    List<CreateMatchResponse> matches = matchService.getAllMatches();
    model.addAttribute("matches", matches);
    model.addAttribute("matchRequest", new CreateMatchRequest(null, null, null));
    return "match-history";
  }

  @PostMapping("/report")
  public String reportMatch(
      @Valid @ModelAttribute CreateMatchRequest matchRequest,
      RedirectAttributes redirectAttributes) {
    matchService.createMatch(matchRequest);
    redirectAttributes.addFlashAttribute("message", "Match reported successfully!");
    return "redirect:/players";
  }
}
