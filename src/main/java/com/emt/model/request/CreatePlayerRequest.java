package com.emt.model.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record CreatePlayerRequest(
    @NotNull(message = "Nickname should not be null.")
        @Size(
            min = 2,
            max = 20,
            message = "The length of the nickname must be from 2 to 20 characters inclusive.")
        String nickname
) {}
