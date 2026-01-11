package com.acc.local.domain.model.auth;

import com.acc.local.dto.auth.UserKeystoneDto;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class UserListResponse {

    private List<UserKeystoneDto> userKeystoneDtos;
    private String nextMarker;
    private String prevMarker;
}