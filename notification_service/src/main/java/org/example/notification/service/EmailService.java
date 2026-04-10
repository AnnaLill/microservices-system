package org.example.notification.service;

import org.example.notification.model.NotificationMessage;

public interface EmailService {
    void send(String email, NotificationMessage messageType);
}
