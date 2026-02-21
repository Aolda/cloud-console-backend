package com.acc.global.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConfigurationProperties(prefix = "mail")
@Getter @Setter
public class EmailProperties {
    private String from;
    private String subject;
    private List<String> adminEmails;
}

