package com.acc.global.config;

import net.javacrumbs.shedlock.core.LockProvider;
import net.javacrumbs.shedlock.provider.jdbctemplate.JdbcTemplateLockProvider;
import net.javacrumbs.shedlock.spring.annotation.EnableSchedulerLock;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.sql.DataSource;

/**
 * 스케줄링 및 분산 락 설정
 * - @EnableScheduling: Spring Scheduling 활성화
 * - @EnableSchedulerLock: ShedLock을 통한 분산 환경 중복 실행 방지
 */
@Configuration
@EnableScheduling
@EnableSchedulerLock(defaultLockAtMostFor = "2h")
public class SchedulerConfig {

    /**
     * ShedLock용 LockProvider 설정
     * JDBC를 사용하여 DB 기반 분산 락 구현
     *
     * @param dataSource MariaDB 데이터소스
     * @return JDBC 기반 Lock Provider
     */
    @Bean
    public LockProvider lockProvider(DataSource dataSource) {
        return new JdbcTemplateLockProvider(
            JdbcTemplateLockProvider.Configuration.builder()
                .withJdbcTemplate(new JdbcTemplate(dataSource))
                .usingDbTime()  // DB 서버 시간 기준 (서버 간 시간 차이 방지)
                .build()
        );
    }
}

