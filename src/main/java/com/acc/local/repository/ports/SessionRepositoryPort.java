package com.acc.local.repository.ports;

import com.acc.local.domain.model.session.SessionData;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

public interface SessionRepositoryPort {

    void save(String sessionId, SessionData data, long ttl, TimeUnit unit);

    Optional<SessionData> findById(String sessionId);

    void deleteById(String sessionId);

    <T> void updateField(String sessionId, String fieldName, T value);

    boolean extendTtl(String sessionId, long ttl, TimeUnit unit);

    boolean exists(String sessionId);
}
