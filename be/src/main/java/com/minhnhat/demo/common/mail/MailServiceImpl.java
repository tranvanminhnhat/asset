package com.minhnhat.demo.common.mail;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
@RequiredArgsConstructor
public class MailServiceImpl implements MailService {
    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Override
    @Async
    public void send(MailPayload payload) {
        try {
            Context context = new Context();

            // Inject variables into template
            payload.getVariables().forEach(context::setVariable);

            // Render HTML
            String html = templateEngine.process(
                    payload.getTemplateType().getTemplateName(),
                    context
            );

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(payload.getTo());
            helper.setSubject(payload.getTemplateType().getSubject());
            helper.setFrom("no-reply@yourdomain.com");
            helper.setText(html, true);

            mailSender.send(message);

        } catch (Exception e) {
            throw new RuntimeException("Send mail failed", e);
        }
    }
}
