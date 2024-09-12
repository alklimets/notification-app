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

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
            if (isEventTypeSupported(eventType)) {
                log.error("Event type {} is not supported", eventType);
                return;
            }
            var message = mailSender.createMimeMessage();
            prepareMessage(event, message, eventType);
            mailSender.send(message);
            log.info("Email has been sent to the recipient successfully");
        } catch (Exception e) {
            log.error("An error occurred during the email sending - {}", e.getMessage());
        }
    }

    private boolean isEventTypeSupported(String eventType) throws IOException {
        return !listFilePaths("src/main/resources/templates/emails").contains(String.format("%s.html", eventType));
    }

    public static List<String> listFilePaths(String directory) throws IOException {
        try (var paths = Files.list(Paths.get(directory))) {
            return paths.filter(Files::isRegularFile)
                    .map(Path::getFileName)
                    .map(Path::toString)
                    .collect(Collectors.toList());
        }
    }

    private void prepareMessage(DomainNotificationKafkaEvent event, MimeMessage message, String eventType) throws MessagingException, UnsupportedEncodingException {
        var helper = new MimeMessageHelper(message);
        helper.setFrom(emailFrom, nameFrom);
        helper.setTo(event.address().getValue());
        helper.setSubject(event.subject().getValue());

        // Thymeleaf Context
        var context = new Context();

        // Properties to show up in Template after stored in Context
        Map<String, Object> properties = new HashMap<>(event.contentMap().getValue());
        properties.put("year", timeSource.getCurrentLocalDate().getYear());
        context.setVariables(properties);

        var html = templateEngine.process(String.format("emails/%s.html", eventType), context);
        helper.setText(html, true);
        log.info(html);
    }
}
