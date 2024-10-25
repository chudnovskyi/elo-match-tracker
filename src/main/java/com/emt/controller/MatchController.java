package com.emt.controller;

import com.emt.model.request.CreateMatchRequest;
import com.emt.model.request.CreatePlayerRequest;
import com.emt.model.response.MatchResponse;
import com.emt.service.MatchService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/matches")
@RequiredArgsConstructor
public class MatchController {

  private final MatchService matchService;

  @GetMapping
  public String getAllMatches(Model model) {
    List<MatchResponse> matches = matchService.getAllMatches();
    model.addAttribute("matches", matches);
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
