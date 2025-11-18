package com.acc.local.domain.model.auth;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class UserListResponse {

    private List<User> users;
    private String nextMarker;
    private String prevMarker;
}