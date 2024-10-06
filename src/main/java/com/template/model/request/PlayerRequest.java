package com.template.model.request;

import jakarta.validation.constraints.NotEmpty;

public record PlayerRequest(@NotEmpty String username) {}
