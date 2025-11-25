package com.acc.local.repository.adapters;

import com.acc.local.entity.OAuthVerificationTokenEntity;
import com.acc.local.repository.jpa.OAuthVerificationTokenJpaRepository;
import com.acc.local.repository.ports.OAuthVerificationTokenRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class OAuthVerificationTokenRepositoryAdapter implements OAuthVerificationTokenRepositoryPort {

    private final OAuthVerificationTokenJpaRepository oAuthVerificationTokenJpaRepository;

    @Override
    public OAuthVerificationTokenEntity save(OAuthVerificationTokenEntity token) {
        return oAuthVerificationTokenJpaRepository.save(token);
    }

    @Override
    public Optional<OAuthVerificationTokenEntity> findFirstByEmailAndUsedFalseOrderByCreatedAtDesc(String email) {
        return oAuthVerificationTokenJpaRepository.findFirstByEmailAndUsedFalseOrderByCreatedAtDesc(email);
    }

    @Override
    public void delete(OAuthVerificationTokenEntity token) {
        oAuthVerificationTokenJpaRepository.delete(token);
    }
}