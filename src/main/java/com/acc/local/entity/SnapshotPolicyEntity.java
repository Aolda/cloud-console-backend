package com.acc.local.entity;

import com.acc.local.domain.enums.IntervalType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * 스냅샷 수명관리자 정책 엔티티
 */
@Entity
@Table(name = "snapshot_policies")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SnapshotPolicyEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name", length = 100, nullable = false)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "volume_id", length = 64, nullable = false)
    private String volumeId;

    @Enumerated(EnumType.STRING)
    @Column(name = "interval_type", nullable = false)
    private IntervalType intervalType;

    @Column(name = "enabled")
    private Boolean enabled;

    @Column(name = "expiration_date")
    private LocalDateTime expirationDate;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "daily_time")
    private LocalTime dailyTime;

    @Column(name = "weekly_day_of_week")
    private Integer weeklyDayOfWeek;

    @Column(name = "weekly_time")
    private LocalTime weeklyTime;

    @Column(name = "monthly_day_of_month")
    private Integer monthlyDayOfMonth;

    @Column(name = "monthly_time")
    private LocalTime monthlyTime;

    @Column(name = "timezone", length = 64)
    private String timezone;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.enabled == null) {
            this.enabled = true;
        }
        if (this.timezone == null) {
            this.timezone = "Asia/Seoul";
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    @Builder
    public SnapshotPolicyEntity(String name, String description, String volumeId,
                                IntervalType intervalType, Boolean enabled, LocalDateTime expirationDate,
                                LocalTime dailyTime, Integer weeklyDayOfWeek, LocalTime weeklyTime,
                                Integer monthlyDayOfMonth, LocalTime monthlyTime, String timezone) {
        this.name = name;
        this.description = description;
        this.volumeId = volumeId;
        this.intervalType = intervalType;
        this.enabled = enabled;
        this.expirationDate = expirationDate;
        this.dailyTime = dailyTime;
        this.weeklyDayOfWeek = weeklyDayOfWeek;
        this.weeklyTime = weeklyTime;
        this.monthlyDayOfMonth = monthlyDayOfMonth;
        this.monthlyTime = monthlyTime;
        this.timezone = timezone;
    }

    public void update(String name, String description, IntervalType intervalType,
                      LocalDateTime expirationDate, LocalTime dailyTime, Integer weeklyDayOfWeek,
                      LocalTime weeklyTime, Integer monthlyDayOfMonth, LocalTime monthlyTime) {
        if (name != null) this.name = name;
        if (description != null) this.description = description;
        if (intervalType != null) this.intervalType = intervalType;
        if (expirationDate != null) this.expirationDate = expirationDate;
        if (dailyTime != null) this.dailyTime = dailyTime;
        if (weeklyDayOfWeek != null) this.weeklyDayOfWeek = weeklyDayOfWeek;
        if (weeklyTime != null) this.weeklyTime = weeklyTime;
        if (monthlyDayOfMonth != null) this.monthlyDayOfMonth = monthlyDayOfMonth;
        if (monthlyTime != null) this.monthlyTime = monthlyTime;
    }

    public void activate() {
        this.enabled = true;
    }

    public void deactivate() {
        this.enabled = false;
    }
}

