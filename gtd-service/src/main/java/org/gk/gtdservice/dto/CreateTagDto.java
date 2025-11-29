package org.gk.gtdservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateTagDto(
        @NotNull Long userId,
        @NotBlank @Size(max = 50) String name
) {
}
