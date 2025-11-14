package com.acc.local.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "keypairs")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@IdClass(KeypairProjectId.class)
public class KeypairEntity {

    @Id
    @Column(name = "keypair_id")
    private String keypairId;

    @Column(name = "keypair_name", nullable = false)
    private String keypairName;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private ProjectEntity project;

    @Builder
    public KeypairEntity(String keypairId, String keypairName, ProjectEntity project) {
        this.keypairId = keypairId;
        this.keypairName = keypairName;
        this.project = project;
    }
}
