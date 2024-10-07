package com.template.controller;

import com.template.model.request.MatchRequest;
import com.template.model.response.MatchResponse;
import com.template.service.MatchService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/matches")
public class MatchController {

    private final MatchService matchService;

    @GetMapping
    public String getAllMatches(Model model) {
        List<MatchResponse> matches = matchService.getAllMatches();
        model.addAttribute("matches", matches);
        return "ranking";
    }

    @PostMapping("/report")
    public String createMatch(@Valid @ModelAttribute MatchRequest request) {
        matchService.createMatch(request);
        return "redirect:/matches";
    }
}
