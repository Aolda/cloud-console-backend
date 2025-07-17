package com.acc.local.domain.model;

import com.acc.local.domain.enums.ProjectStatus;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 *  프로젝트 도메인 모델
 * - Keystone에서 실제 생성/관리되는 프로젝트
 * - 생성 플로우: 1) createPending으로 PENDING 상태 생성 → 2) Keystone API 호출 → 3) assignKeystoneId로 ID 할당 및 ACTIVE 상태 전환
 */
@Getter
public class Project {

    private String keystoneProjectId;         // Keystone에서 생성 후 받은 실제 프로젝트 ID (nullable)
    private String name;                      // 프로젝트명 (Keycloak 클레임 생성용)
    private String description;
    private boolean enabled;                  // 프로젝트 활성화 상태
    private ProjectStatus status;             // 백엔드 관리용 상태
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // PENDING 상태 프로젝트 생성용 생성자
    private Project(String name, String description) {
        validateProjectName(name);
        this.keystoneProjectId = null;  // 아직 Keystone에서 생성 전
        this.name = name;
        this.description = description;
        this.enabled = true;
        this.status = ProjectStatus.PENDING;  // 생성 대기 상태
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // 메인 플로우: PENDING 상태 프로젝트 생성
    public static Project createPending(String name, String description) {
        return new Project(name, description);
    }

    // Keystone 생성 완료 후 ID 할당
    public void assignKeystoneId(String keystoneProjectId, boolean enabled) {
        if (this.keystoneProjectId != null) {
            throw new IllegalStateException("Keystone 프로젝트 ID가 이미 할당되었습니다.");
        }
        validateKeystoneProjectId(keystoneProjectId);
        this.keystoneProjectId = keystoneProjectId;
        this.enabled = enabled;  // Keystone 상태 반영
        this.status = ProjectStatus.ACTIVE;
        this.updatedAt = LocalDateTime.now();
    }

    public void suspend() {
        if (this.status == ProjectStatus.SUSPENDED) {
            throw new IllegalStateException("프로젝트는 이미 정지 상태입니다.");
        }
        this.status = ProjectStatus.SUSPENDED;
        this.updatedAt = LocalDateTime.now();
    }

    public void activate() {
        this.status = ProjectStatus.ACTIVE;
        this.updatedAt = LocalDateTime.now();
    }

    public void deactivate() {
        this.status = ProjectStatus.INACTIVE;
        this.updatedAt = LocalDateTime.now();
    }


    public void markAsDeleted() {
        this.status = ProjectStatus.DELETED;
        this.updatedAt = LocalDateTime.now();
    }

    public boolean isKeystoneCreated() {
        return this.keystoneProjectId != null;
    }

    public boolean isAccessible() {
        return this.status == ProjectStatus.ACTIVE && this.enabled && isKeystoneCreated();
    }

    public boolean canCreateResources() {
        return this.status == ProjectStatus.ACTIVE && this.enabled && isKeystoneCreated();
    }


    public boolean isKeystoneEnabled() {
        return this.enabled;
    }


    // 검증 로직
    private void validateKeystoneProjectId(String keystoneProjectId) {
        if (keystoneProjectId == null || keystoneProjectId.trim().isEmpty()) {
            throw new IllegalArgumentException("Keystone 프로젝트 ID는 필수입니다.");
        }
        // UUID 형식 검증 (Keystone은 UUID 형태의 프로젝트 ID 사용)
        if (!keystoneProjectId.matches("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$")) {
            throw new IllegalArgumentException("Keystone 프로젝트 ID는 UUID 형식이어야 합니다.");
        }
    }

    private void validateProjectName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("프로젝트명은 필수입니다.");
        }
        if (name.length() < 2 || name.length() > 64) {
            throw new IllegalArgumentException("프로젝트명은 2-64자 사이여야 합니다.");
        }
        // Keystone 프로젝트명 규칙 검증
        if (!name.matches("^[a-zA-Z0-9_-]+$")) {
            throw new IllegalArgumentException("프로젝트명은 영문, 숫자, 언더스코어, 하이픈만 사용 가능합니다.");
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Project project = (Project) obj;
        return Objects.equals(keystoneProjectId, project.keystoneProjectId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(keystoneProjectId);
    }

}
