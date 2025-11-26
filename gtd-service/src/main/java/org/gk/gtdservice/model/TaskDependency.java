package org.gk.gtdservice.model;

public record TaskDependency(
        Long taskId,
        Long dependsOnTaskId
) {
}
