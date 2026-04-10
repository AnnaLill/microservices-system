package org.example.notification.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.notification.model.NotificationMessage;
import org.example.notification.model.UserEventOperation;
import org.example.notification.service.EmailService;
import org.example.notification.service.NotificationService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final EmailService emailService;

    @Override
    public void sendByOperation(String email, UserEventOperation operation) {
        NotificationMessage messageType = switch (operation) {
            case CREATE -> NotificationMessage.CREATE_NOTIFICATION;
            case DELETE -> NotificationMessage.DELETE_NOTIFICATION;
        };
        emailService.send(email, messageType);
    }
}
