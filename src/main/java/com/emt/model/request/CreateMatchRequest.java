package com.emt.model.request;

import com.emt.entity.Player;

public record CreateMatchRequest(Player playerOneName, Player playerTwoName, String winner) {}
