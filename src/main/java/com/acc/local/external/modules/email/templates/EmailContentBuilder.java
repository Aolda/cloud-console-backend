package com.acc.local.external.modules.email.templates;

import com.acc.global.exception.email.EmailErrorCode;
import com.acc.global.exception.email.EmailException;
import com.acc.local.external.dto.google.GoogleFormRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.Context;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.spring6.SpringTemplateEngine;

@Component
@RequiredArgsConstructor
public class EmailContentBuilder {

    private final SpringTemplateEngine templateEngine;

    public String createHtmlContext(GoogleFormRequest request) {
        try {
            Context context = new Context();
            context.setVariable("timestamp", request.timestamp());
            context.setVariable("name", request.name());
            context.setVariable("email", request.email());
            context.setVariable("department", request.department());
            context.setVariable("studentId", request.studentId());
            context.setVariable("projectName", request.projectName());
            context.setVariable("projectId", request.projectId());
            context.setVariable("projectPurpose", request.projectPurpose());
            context.setVariable("cpuCores", request.cpuCores());
            context.setVariable("memorySize", request.memorySize());
            context.setVariable("volumeSize", request.volumeSize());
            return templateEngine.process("email/project-confirmation", context);
        } catch (TemplateProcessingException e) {
            throw new EmailException(EmailErrorCode.EMAIL_TEMPLATE_PROCESSING_FAILURE, e);
        }
    }

    public String createProjectRequestCreatedHtmlContext(
            String requesterName,
            String requesterEmail,
            String projectName,
            String projectRequestId,
            String projectDescription
    ) {
        try {
            Context context = new Context();
            context.setVariable("name", requesterName);
            context.setVariable("email", requesterEmail);
            context.setVariable("projectName", projectName);
            context.setVariable("projectRequestId", projectRequestId);
            context.setVariable("projectDescription", projectDescription);
            return templateEngine.process("email/project-request-created", context);
        } catch (TemplateProcessingException e) {
            throw new EmailException(EmailErrorCode.EMAIL_TEMPLATE_PROCESSING_FAILURE, e);
        }
    }

    public String createApprovalHtmlContext(
            String requesterName,
            String requesterEmail,
            String projectName,
            String createdProjectId
    ) {
        try {
            Context context = new Context();
            context.setVariable("name", requesterName);
            context.setVariable("email", requesterEmail);
            context.setVariable("projectName", projectName);
            context.setVariable("createdProjectId", createdProjectId);
            return templateEngine.process("email/project-approval", context);
        } catch (TemplateProcessingException e) {
            throw new EmailException(EmailErrorCode.EMAIL_TEMPLATE_PROCESSING_FAILURE, e);
        }
    }

    public String createRejectionHtmlContext(
            String requesterName,
            String requesterEmail,
            String projectName,
            String rejectReason
    ) {
        try {
            Context context = new Context();
            context.setVariable("name", requesterName);
            context.setVariable("email", requesterEmail);
            context.setVariable("projectName", projectName);
            context.setVariable("rejectReason", rejectReason);
            return templateEngine.process("email/project-rejection", context);
        } catch (TemplateProcessingException e) {
            throw new EmailException(EmailErrorCode.EMAIL_TEMPLATE_PROCESSING_FAILURE, e);
        }
    }

    public String createProjectDirectlyCreatedHtmlContext(String projectId, String projectName, String ownerName) {
        try {
            Context context = new Context();
            context.setVariable("projectId", projectId);
            context.setVariable("projectName", projectName);
            context.setVariable("ownerName", ownerName);
            return templateEngine.process("email/project-created", context);
        } catch (TemplateProcessingException e) {
            throw new EmailException(EmailErrorCode.EMAIL_TEMPLATE_PROCESSING_FAILURE, e);
        }
    }
}
