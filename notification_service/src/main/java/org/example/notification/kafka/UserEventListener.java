package org.example.notification.kafka;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.notification.dto.UserEvent;
import org.example.notification.model.UserEventOperation;
import org.example.notification.service.NotificationService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserEventListener {

    private final NotificationService notificationService;

    @Value("${app.kafka.user-events-topic}")
    private String userEventsTopic;

    @KafkaListener(topics = "${app.kafka.user-events-topic}")
    public void onUserEvent(@Valid UserEvent event) {
        try {
            UserEventOperation operation = UserEventOperation.fromString(event.operation());
            notificationService.sendByOperation(event.email(), operation);
            log.info("Notification sent for topic={}, operation={}, email={}",
                    userEventsTopic, operation, event.email());
        } catch (RuntimeException ex) {
            log.error("Failed to process user event from topic={}. event={}, error={}",
                    userEventsTopic, event, ex.getMessage(), ex);
        }
    }
}
