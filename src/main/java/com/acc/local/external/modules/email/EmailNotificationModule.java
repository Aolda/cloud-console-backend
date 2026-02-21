package com.acc.local.external.modules.email;

import com.acc.global.properties.EmailProperties;
import com.acc.local.external.dto.google.GoogleFormRequest;
import com.acc.local.external.modules.email.templates.EmailContentBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EmailNotificationModule {

    private final EmailContentBuilder contentBuilder;
    private final EmailSenderModule emailSender;
    private final EmailProperties emailProperties;

    public void sendNewProjectNotification(GoogleFormRequest request) {
        String emailBody = contentBuilder.createHtmlContext(request);
        String[] adminEmails = getAdminEmailsArray();
        emailSender.sendEmailWithCc(request.email(), adminEmails, emailProperties.getSubject(), emailBody);
    }

    public void sendProjectRequestCreatedNotification(
            String requesterName,
            String requesterEmail,
            String projectName,
            String projectRequestId,
            String projectDescription
    ) {
        String emailBody = contentBuilder.createProjectRequestCreatedHtmlContext(
                requesterName, requesterEmail, projectName, projectRequestId, projectDescription
        );
        String subject = "[아올다 클라우드] 프로젝트 신청이 접수되었습니다 - " + projectName;
        String[] adminEmails = getAdminEmailsArray();
        emailSender.sendEmailWithCc(requesterEmail, adminEmails, subject, emailBody);
    }

    public void sendProjectApprovalNotification(
            String requesterName,
            String requesterEmail,
            String projectName,
            String createdProjectId
    ) {
        String emailBody = contentBuilder.createApprovalHtmlContext(
                requesterName, requesterEmail, projectName, createdProjectId
        );
        String subject = "[아올다 클라우드] 프로젝트 신청이 승인되었습니다 - " + projectName;
        String[] adminEmails = getAdminEmailsArray();
        emailSender.sendEmailWithCc(requesterEmail, adminEmails, subject, emailBody);
    }

    public void sendProjectRejectionNotification(
            String requesterName,
            String requesterEmail,
            String projectName,
            String rejectReason
    ) {
        String emailBody = contentBuilder.createRejectionHtmlContext(
                requesterName, requesterEmail, projectName, rejectReason
        );
        String subject = "[아올다 클라우드] 프로젝트 신청이 거부되었습니다 - " + projectName;
        String[] adminEmails = getAdminEmailsArray();
        emailSender.sendEmailWithCc(requesterEmail, adminEmails, subject, emailBody);
    }

    public void sendProjectDirectlyCreatedNotification(String projectId, String projectName, String ownerName, String ownerEmail) {
        String emailBody = contentBuilder.createProjectDirectlyCreatedHtmlContext(projectId, projectName, ownerName);
        String subject = "[아올다 클라우드] 프로젝트가 생성되었습니다 - " + projectName;
        String[] adminEmails = getAdminEmailsArray();
        emailSender.sendEmailWithCc(ownerEmail, adminEmails, subject, emailBody);
    }

    /**
     * 관리자 이메일 배열 반환
     */
    private String[] getAdminEmailsArray() {
        if (emailProperties.getAdminEmails() != null && !emailProperties.getAdminEmails().isEmpty()) {
            return emailProperties.getAdminEmails().toArray(new String[0]);
        }
        return new String[0];
    }
}
