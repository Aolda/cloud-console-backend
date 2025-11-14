package com.acc.local.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "projects")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProjectEntity {

    @Id
    @Column(name = "project_id")
    private String projectId;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<KeypairEntity> keypairs = new ArrayList<>();

    @Builder
    public ProjectEntity(String projectId) {
        this.projectId = projectId;
    }
}
