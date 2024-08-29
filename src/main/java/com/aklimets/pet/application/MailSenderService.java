package com.aklimets.pet.application;

import com.aklimets.pet.domain.event.DomainNotificationKafkaEvent;
import com.aklimets.pet.util.datetime.TimeSource;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class MailSenderService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private SpringTemplateEngine templateEngine;

    @Value("${notifications.email.from}")
    private String emailFrom;

    @Value("${notifications.name.from}")
    private String nameFrom;

    @Autowired
    private TimeSource timeSource;

    @Async
    public void processDomainEvent(DomainNotificationKafkaEvent event, String eventType) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            prepareMessage(event, message, eventType);
            mailSender.send(message);
            log.info("Email has been sent to the recipient successfully");
        } catch (Exception e) {
            log.error("An error occurred during the email sending - {}", e.getMessage());
        }
    }

    private void prepareMessage(DomainNotificationKafkaEvent event, MimeMessage message, String eventType) throws MessagingException, UnsupportedEncodingException {
        MimeMessageHelper helper = new MimeMessageHelper(message);
        helper.setFrom(emailFrom, nameFrom);
        helper.setTo(event.address().getValue());
        helper.setSubject(event.subject().getValue());

        // Thymeleaf Context
        Context context = new Context();

        // Properties to show up in Template after stored in Context
        Map<String, Object> properties = new HashMap<>(event.contentMap().getValue());
        properties.put("year", timeSource.getCurrentLocalDate().getYear());
        context.setVariables(properties);

        String html = templateEngine.process(String.format("emails/%s.html", eventType), context);
        helper.setText(html, true);
        log.info(html);
    }
}
