package com.acc.local.service.modules.email.templates;

import com.acc.global.exception.email.EmailErrorCode;
import com.acc.global.exception.email.EmailException;
import com.acc.local.dto.google.GoogleFormRequest;
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
}
