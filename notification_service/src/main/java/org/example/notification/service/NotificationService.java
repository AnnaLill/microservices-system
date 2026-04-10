package org.example.notification.service;

import org.example.notification.model.UserEventOperation;

public interface NotificationService {
    void sendByOperation(String email, UserEventOperation operation);
}
