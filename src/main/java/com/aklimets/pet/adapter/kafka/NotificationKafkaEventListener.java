package com.aklimets.pet.adapter.kafka;

import com.aklimets.pet.domain.event.DomainNotificationKafkaEvent;
import com.aklimets.pet.util.datetime.TimeSource;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class NotificationKafkaEventListener {

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

    @KafkaListener(topics = "${notification.topic.name}", groupId = "${notification.group.name}")
    public void consume(DomainNotificationKafkaEvent event) {
        log.info("Received notification event: " + event);
        try {
            MimeMessage message = mailSender.createMimeMessage();
            prepareMessage(event, message);
            mailSender.send(message);
            log.info("Email has been sent to the recipient successfully");
        } catch (Exception e) {
            log.error("An error occurred during the email sending - {}", e.getMessage());
        }
    }

    private void prepareMessage(DomainNotificationKafkaEvent event, MimeMessage message) throws MessagingException, UnsupportedEncodingException {
        MimeMessageHelper helper = new MimeMessageHelper(message);
        helper.setFrom(emailFrom, nameFrom);
        helper.setTo(event.address().getValue());
        helper.setSubject(event.subject().getValue());

        // Thymeleaf Context
        Context context = new Context();

        // Properties to show up in Template after stored in Context
        Map<String, Object> properties = new HashMap<>();
        properties.put("content", event.content().getValue());
        properties.put("year", timeSource.getCurrentLocalDate().getYear());
        context.setVariables(properties);

        String html = templateEngine.process("emails/notification.html", context);
        helper.setText(html, true);
        log.info(html);
    }
}
