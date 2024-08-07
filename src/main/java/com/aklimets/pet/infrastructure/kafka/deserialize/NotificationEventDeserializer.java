package com.aklimets.pet.infrastructure.kafka.deserialize;

import com.aklimets.pet.domain.event.DomainNotificationKafkaEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Deserializer;

import java.io.IOException;

public class NotificationEventDeserializer implements Deserializer<DomainNotificationKafkaEvent> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public DomainNotificationKafkaEvent deserialize(String s, byte[] bytes) {
        try {
            return objectMapper.readValue(bytes, DomainNotificationKafkaEvent.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
