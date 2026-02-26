package com.acc.local.service.modules.outbox;

public record ProjectCreatedEvent(
        String projectId,
        String projectName,
        String projectOwnerId,
        String createdByUserId,
        String email
) {}
