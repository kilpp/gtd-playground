package org.gk.gtdservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateReferenceDto(
        @NotNull Long userId,
        @NotBlank String title,
        String body,
        String url,
        String fileHint
) {
}
