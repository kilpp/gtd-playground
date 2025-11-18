package org.gk.gtdservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record CreateProjectDto(
        @NotNull Long userId,
        Long areaId,
        @NotBlank @Size(max = 200) String title,
        @Size(max = 500) String outcome,
        @Size(max = 2000) String notes,
        @NotBlank @Pattern(regexp = "active|on_hold|someday|completed|dropped") String status,
        LocalDate dueDate
) {
}
