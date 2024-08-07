package com.aklimets.pet.domain.event;

import com.aklimets.pet.buildingblock.interfaces.DomainEvent;
import com.aklimets.pet.domain.model.attribute.NotificationContent;
import com.aklimets.pet.domain.model.attribute.NotificationSubject;
import com.aklimets.pet.model.attribute.EmailAddress;
import com.aklimets.pet.model.attribute.RequestId;

public record DomainNotificationKafkaEvent(EmailAddress address,
                                           NotificationSubject subject,
                                           NotificationContent content,
                                           RequestId requestId) implements DomainEvent {

    @Override
    public String toString() {
        return "DomainNotificationKafkaEvent{" +
                "address=" + address.getValue() +
                ", subject=" + subject.getValue() +
                ", content=" + content.getValue() +
                ", requestId=" + requestId.getValue() +
                '}';
    }
}
