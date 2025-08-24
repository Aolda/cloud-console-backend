package com.acc.local.service.adapters.auth;
import com.acc.local.service.modules.auth.AuthModule;
import com.acc.local.service.ports.AuthPort;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Service
@Primary
@RequiredArgsConstructor
public class AuthAdapter implements AuthPort {
    private final AuthModule authModule;

    @Override
    public String issueToken() {
        return authModule.issueToken();
    }
}
