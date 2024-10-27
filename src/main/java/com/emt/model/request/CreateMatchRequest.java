package com.emt.model.request;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record CreateMatchRequest(@NotNull Long winnerId, @NotNull Long loserId) {
}
