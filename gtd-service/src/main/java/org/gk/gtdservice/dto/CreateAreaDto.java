package org.gk.gtdservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateAreaDto(
        @NotNull Long userId,
        @NotBlank @Size(max = 100) String name,
        @Size(max = 500) String description
) {
}

