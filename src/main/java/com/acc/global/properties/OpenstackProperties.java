package com.acc.global.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "openstack")
@Getter
@Setter
public class OpenstackProperties {
	private String saUsername;
	private String saPassword;
}
