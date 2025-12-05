package com.acc.local.external.dto.keystone;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.Objects;
import java.util.Map;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class KeystoneProject {

    private String id; // keystone projectId
    private String name;
    private String description;
    private String domainId;
    private String parentId;
    private Boolean enabled;
    private Boolean isDomain;
    private List<String> tags;

    @Builder
    public KeystoneProject(String name, String description) {
        this.name = name;
        this.description = description;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        KeystoneProject keystoneProject = (KeystoneProject) obj;
        return Objects.equals(id, keystoneProject.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

}
