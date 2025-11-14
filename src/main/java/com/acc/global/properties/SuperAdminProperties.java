package com.acc.global.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "super-admin")
@Getter
@Setter
public class SuperAdminProperties {
	private String userId;
	private String phoneNumber;
    private String userName;
}