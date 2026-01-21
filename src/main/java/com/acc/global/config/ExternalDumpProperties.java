package com.acc.global.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "acc.dump")
public class ExternalDumpProperties {
    /**
     * 외부 API 응답 덤프 활성화 여부. 기본 false.
     */
    private boolean enabled = false;

    /**
     * 덤프 파일 저장 디렉터리. 기본 "external-dumps".
     */
    private String dir = "external-dumps";

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getDir() {
        return dir;
    }

    public void setDir(String dir) {
        this.dir = dir;
    }
}

