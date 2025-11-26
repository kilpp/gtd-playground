package org.gk.gtdservice.dto;

import jakarta.validation.constraints.NotNull;

public record CreateTaskDependencyDto(
        @NotNull Long taskId,
        @NotNull Long dependsOnTaskId
) {
}
