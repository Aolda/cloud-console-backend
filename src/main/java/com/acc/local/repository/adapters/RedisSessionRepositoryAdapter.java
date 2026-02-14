package com.acc.local.repository.adapters;

import com.acc.global.exception.session.SessionErrorCode;
import com.acc.global.exception.session.SessionException;
import com.acc.local.domain.model.session.KeycloakTokens;
import com.acc.local.domain.model.session.KeystoneTokens;
import com.acc.local.domain.model.session.SessionConstants;
import com.acc.local.domain.model.session.SessionData;
import com.acc.local.domain.model.session.UserInfo;
import com.acc.local.repository.ports.SessionRepositoryPort;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@Primary
@RequiredArgsConstructor
public class RedisSessionRepositoryAdapter implements SessionRepositoryPort {

    private final RedisTemplate<String, String> sessionRedisTemplate;
    private final ObjectMapper objectMapper;

    /**
     * 세션을 Redis Hash로 저장한다. null 필드는 저장하지 않습니다.
     * putAll + expire를 같은 connection에서 실행됩니다.
     *
     * @param sessionId 세션 식별자
     * @param data      저장할 세션 데이터
     * @param ttl       만료 시간 값
     * @param unit      만료 시간 단위
     */
    @Override
    public void save(String sessionId, SessionData data, long ttl, TimeUnit unit) {
        String key = buildKey(sessionId);
        try {
            Map<String, String> fields = buildFieldMap(data);
            sessionRedisTemplate.execute(new SessionCallback<>() {
                @Override
                @SuppressWarnings("unchecked")
                public Object execute(org.springframework.data.redis.core.RedisOperations operations) {
                    operations.opsForHash().putAll(key, fields);
                    operations.expire(key, ttl, unit);
                    return null;
                }
            });
        } catch (Exception e) {
            log.error("세션 저장 실패 - sessionId: {}", sessionId, e);
            throw new SessionException(SessionErrorCode.SESSION_SAVE_FAILED, e);
        }
    }

    /**
     * sessionId로 세션을 조회합니다.
     * Redis Hash의 모든 필드를 읽어 SessionData 도메인 객체로 변환합니다.
     *
     * @param sessionId 세션 식별자
     * @return 세션 데이터 (만료되었거나 없으면 Optional.empty())
     */
    @Override
    public Optional<SessionData> findById(String sessionId) {
        String key = buildKey(sessionId);
        try {
            Map<String, String> entries = hashOps().entries(key);
            if (entries.isEmpty()) {
                return Optional.empty();
            }
            return Optional.of(toSessionData(entries));
        } catch (Exception e) {
            log.error("세션 조회 실패 - sessionId: {}", sessionId, e);
            throw new SessionException(SessionErrorCode.SESSION_READ_FAILED, e);
        }
    }

    /**
     * 세션을 삭제한다. Redis key 자체를 제거하므로 모든 Hash 필드가 함께 삭제됩니다.
     *
     * @param sessionId 세션 식별자
     */
    @Override
    public void deleteById(String sessionId) {
        String key = buildKey(sessionId);
        try {
            sessionRedisTemplate.delete(key);
        } catch (Exception e) {
            log.error("세션 삭제 실패 - sessionId: {}", sessionId, e);
            throw new SessionException(SessionErrorCode.SESSION_DELETE_FAILED, e);
        }
    }

    /**
     * 세션의 특정 필드 하나만 업데이트한다.
     *
     * @param sessionId 세션 식별자
     * @param fieldName 업데이트할 Hash 필드명 (SessionConstants 참조)
     * @param value     저장할 값 (String이면 그대로, 객체면 JSON 직렬화)
     * @throws SessionException 세션 미존재 또는 업데이트 실패 시
     */
    @Override
    public <T> void updateField(String sessionId, String fieldName, T value) {
        String key = buildKey(sessionId);
        try {
            if (!keyExists(key)) {
                throw new SessionException(SessionErrorCode.SESSION_NOT_FOUND);
            }
            hashOps().put(key, fieldName, serialize(value));
        } catch (SessionException e) {
            throw e;
        } catch (Exception e) {
            log.error("세션 필드 업데이트 실패 - sessionId: {}, field: {}", sessionId, fieldName, e);
            throw new SessionException(SessionErrorCode.SESSION_SAVE_FAILED, e);
        }
    }

    /**
     * 세션의 TTL을 연장한다.
     *
     * @param sessionId 세션 식별자
     * @param ttl       새로운 만료 시간 값
     * @param unit      만료 시간 단위
     * @return TTL 연장 성공 여부 (세션 미존재 시 false)
     * @throws SessionException Redis 장애 시
     */
    @Override
    public boolean extendTtl(String sessionId, long ttl, TimeUnit unit) {
        String key = buildKey(sessionId);
        try {
            Boolean result = sessionRedisTemplate.expire(key, ttl, unit);
            return result != null && result;
        } catch (Exception e) {
            log.error("세션 TTL 연장 실패 - sessionId: {}", sessionId, e);
            throw new SessionException(SessionErrorCode.SESSION_SAVE_FAILED, e);
        }
    }

    /**
     * 세션 존재 여부를 확인한다.
     *
     * @param sessionId 세션 식별자
     * @return 존재 여부 (TTL 만료 시 false)
     * @throws SessionException Redis 장애 시
     */
    @Override
    public boolean exists(String sessionId) {
        String key = buildKey(sessionId);
        try {
            return keyExists(key);
        } catch (Exception e) {
            log.error("세션 존재 확인 실패 - sessionId: {}", sessionId, e);
            throw new SessionException(SessionErrorCode.SESSION_READ_FAILED, e);
        }
    }

    // ==================== Helper Methods ====================

    private HashOperations<String, String, String> hashOps() {
        return sessionRedisTemplate.opsForHash();
    }

    private String buildKey(String sessionId) {
        return SessionConstants.SESSION_KEY_PREFIX + sessionId;
    }

    private boolean keyExists(String key) {
        Boolean result = sessionRedisTemplate.hasKey(key);
        return result != null && result;
    }

    private String serialize(Object value) throws JsonProcessingException {
        if (value instanceof String) {
            return (String) value;
        }
        return objectMapper.writeValueAsString(value);
    }

    private <T> T deserialize(String json, Class<T> type) throws JsonProcessingException {
        return objectMapper.readValue(json, type);
    }

    private Map<String, String> buildFieldMap(SessionData data) throws JsonProcessingException {
        Map<String, String> fields = new HashMap<>();
        putIfNotNull(fields, SessionConstants.FIELD_KEYCLOAK_TOKENS, data.getKeycloakTokens());
        putIfNotNull(fields, SessionConstants.FIELD_KEYSTONE_TOKENS, data.getKeystoneTokens());
        putIfNotNull(fields, SessionConstants.FIELD_KEYCLOAK_USER_ID, data.getKeycloakUserId());
        putIfNotNull(fields, SessionConstants.FIELD_KEYSTONE_USER_ID, data.getKeystoneUserId());
        putIfNotNull(fields, SessionConstants.FIELD_USER_INFO, data.getUserInfo());
        return fields;
    }

    private void putIfNotNull(Map<String, String> fields, String fieldName, Object value)
            throws JsonProcessingException {
        if (value != null) {
            fields.put(fieldName, serialize(value));
        }
    }

    private SessionData toSessionData(Map<String, String> entries) throws JsonProcessingException {
        return SessionData.builder()
                .keycloakTokens(deserializeIfPresent(entries, SessionConstants.FIELD_KEYCLOAK_TOKENS, KeycloakTokens.class))
                .keystoneTokens(deserializeIfPresent(entries, SessionConstants.FIELD_KEYSTONE_TOKENS, KeystoneTokens.class))
                .keycloakUserId(entries.get(SessionConstants.FIELD_KEYCLOAK_USER_ID))
                .keystoneUserId(entries.get(SessionConstants.FIELD_KEYSTONE_USER_ID))
                .userInfo(deserializeIfPresent(entries, SessionConstants.FIELD_USER_INFO, UserInfo.class))
                .build();
    }

    private <T> T deserializeIfPresent(Map<String, String> entries, String fieldName, Class<T> type)
            throws JsonProcessingException {
        String json = entries.get(fieldName);
        return json != null ? deserialize(json, type) : null;
    }
}
