package com.acc.local.repository.ports;

import com.acc.local.entity.OAuthVerificationTokenEntity;

import java.util.Optional;

public interface OAuthVerificationTokenRepositoryPort {

    OAuthVerificationTokenEntity save(OAuthVerificationTokenEntity token);

    Optional<OAuthVerificationTokenEntity> findFirstByEmailAndUsedFalseOrderByCreatedAtDesc(String email);

    void delete(OAuthVerificationTokenEntity token);
}