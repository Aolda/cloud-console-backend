package com.acc.local.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.acc.local.domain.enums.project.ProjectRequestStatus;
import com.acc.local.domain.enums.project.ProjectRequestType;
import com.acc.local.domain.model.auth.User;

@Entity
@Table(name = "projects")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProjectEntity {

    @Id
    @Column(name = "project_id")
    private String projectId;

    @Column(name = "owner_keystone_id")
    private String ownerKeystoneId;

    @Column(name = "project_type")
    private ProjectRequestType projectType;

    @Column(name = "qouta_vcpu_count")
    private Long quotaVCpuCount;

    @Column(name = "qouta_vram_mb")
    private Long quotaVRamMB;

    @Column(name = "qouta_insstance_count")
    private Long quotaInstanceCount;

    @Column(name = "qouta_storage_gb")
    private Long quotaStorageGB;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<KeypairEntity> keypairs = new ArrayList<>();

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProjectParticipantEntity> participants = new ArrayList<>();

    @Builder
    public ProjectEntity(String projectId) {
        this.projectId = projectId;
    }
}
