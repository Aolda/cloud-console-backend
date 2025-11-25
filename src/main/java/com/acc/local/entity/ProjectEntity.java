package com.acc.local.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.acc.local.domain.enums.project.ProjectRequestType;

@Entity
@Table(name = "projects")
@Getter @Setter
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
    public ProjectEntity(String projectId, String ownerKeystoneId, ProjectRequestType projectType, Long quotaVCpuCount,
        Long quotaVRamMB, Long quotaInstanceCount, Long quotaStorageGB, List<ProjectParticipantEntity> participants) {
        this.projectId = projectId;
        this.ownerKeystoneId = ownerKeystoneId;
        this.projectType = projectType;
        this.quotaVCpuCount = quotaVCpuCount;
        this.quotaVRamMB = quotaVRamMB;
        this.quotaInstanceCount = quotaInstanceCount;
        this.quotaStorageGB = quotaStorageGB;
        this.createdAt = LocalDateTime.now();
    }
}
