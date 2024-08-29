package com.aklimets.pet.adapter.kafka;

import com.aklimets.pet.application.MailSenderService;
import com.aklimets.pet.domain.event.DomainNotificationKafkaEvent;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@AllArgsConstructor
public class NotificationKafkaEventListener {

    private static final String EVENT_TYPE_HEADER_KEY = "EventType";

    private final MailSenderService mailSenderService;

    @KafkaListener(topics = "${notification.topic.name}", groupId = "${notification.group.name}")
    public void consume(ConsumerRecord<String, DomainNotificationKafkaEvent> record) {
        String eventType = new String(record.headers().lastHeader(EVENT_TYPE_HEADER_KEY).value());
        log.info("Received notification event: {}", record.value());
        log.info("Event type {}", eventType);
        mailSenderService.processDomainEvent(record.value(), eventType);
    }
}
