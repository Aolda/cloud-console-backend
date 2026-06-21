package com.acc.local.dto.project;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import com.acc.local.domain.enums.project.ProjectRequestStatus;
import com.acc.local.domain.enums.project.ProjectRequestType;
import com.acc.local.domain.enums.project.ProjectRole;
import com.acc.local.dto.auth.UserKeystoneDto;

class ProjectResponseTest {

    @Test
    void fromProjectRequestIncludesRequesterEmailInOwnerAndParticipant() {
        ProjectRequestDto request = ProjectRequestDto.builder()
                .projectName("project-name")
                .projectType(ProjectRequestType.ETC)
                .status(ProjectRequestStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();
        UserKeystoneDto requester = UserKeystoneDto.builder()
                .id("user-id")
                .name("user-name")
                .email("user@ajou.ac.kr")
                .build();

        ProjectResponse response = ProjectResponse.from(request, requester);

        assertThat(response.createdBy().userEmail()).isEqualTo("user@ajou.ac.kr");
        assertThat(response.participants()).hasSize(1);
        assertThat(response.participants().get(0).userEmail()).isEqualTo("user@ajou.ac.kr");
        assertThat(response.participants().get(0).role()).isEqualTo(ProjectRole.PROJECT_ADMIN);
    }
}
