package com.emt.model.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record CreatePlayerRequest(
    @NotNull(message = "Nickname should not be null.")
        @Size(
            min = 5,
            max = 50,
            message = "The length of the nickname must be from 5 to 50 characters inclusive.")
        String nickname) {}
