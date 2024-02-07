package com.template.model.request;

import jakarta.validation.constraints.NotNull;

public record UserRequest(@NotNull String firstName, @NotNull String lastName) {}
