package com.aklimets.pet.domain.event;

import com.aklimets.pet.domain.model.attribute.NotificationContentMap;
import com.aklimets.pet.domain.model.attribute.NotificationSubject;
import com.aklimets.pet.event.DomainEvent;
import com.aklimets.pet.model.attribute.EmailAddress;
import com.aklimets.pet.model.attribute.RequestId;

public record DomainNotificationKafkaEvent(EmailAddress address,
                                           NotificationSubject subject,
                                           NotificationContentMap contentMap,
                                           RequestId requestId) implements DomainEvent {

    @Override
    public String toString() {
        return "DomainNotificationKafkaEvent{" +
                "address=" + address.getValue() +
                ", subject=" + subject.getValue() +
                ", content=" + contentMap.getValue() +
                ", requestId=" + requestId.getValue() +
                '}';
    }
}
