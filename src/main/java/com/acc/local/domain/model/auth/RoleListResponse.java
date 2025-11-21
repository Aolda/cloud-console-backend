package com.acc.local.domain.model.auth;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class RoleListResponse {

    private List<Role> roles;
    private String nextMarker;
    private String prevMarker;
}