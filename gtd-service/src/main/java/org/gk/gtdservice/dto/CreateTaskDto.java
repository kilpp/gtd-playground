package org.gk.gtdservice.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.Instant;

public record CreateTaskDto(
        @NotNull Long userId,
        Long projectId,
        Long contextId,
        @NotBlank @Size(max = 500) String title,
        @Size(max = 2000) String notes,
        @NotBlank @Pattern(regexp = "inbox|next|waiting|scheduled|someday|reference|done|dropped") String status,
        @Min(1) Integer priority,
        @Min(1) @Max(5) Integer energy,
        @Min(1) Integer durationEstMin,
        Instant dueAt,
        Instant deferUntil,
        @Size(max = 200) String waitingOn,
        Instant waitingSince,
        Integer orderIndex
) {
}
