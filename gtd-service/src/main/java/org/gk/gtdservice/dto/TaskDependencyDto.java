package org.gk.gtdservice.dto;

public record TaskDependencyDto(
        Long taskId,
        Long dependsOnTaskId
) {
}
