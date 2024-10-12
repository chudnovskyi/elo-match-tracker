package com.emt.model.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public record CreatePlayerRequest(
    @NotEmpty(message = "Username should not be empty.")
        @Size(
            min = 5,
            max = 50,
            message = "The length of the username must be from 5 to 50 characters inclusive.")
        String username) {}
