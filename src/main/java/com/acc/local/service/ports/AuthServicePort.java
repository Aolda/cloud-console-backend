package com.acc.local.service.ports;

import com.acc.local.dto.auth.LoginedUserProfileResponse;

public interface AuthServicePort {
    LoginedUserProfileResponse getUserLoginedProfile(String userId);
}
