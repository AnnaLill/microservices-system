package org.example.notification.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.notification.dto.SendNotificationRequest;
import org.example.notification.model.UserEventOperation;
import org.example.notification.service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @PostMapping("/email")
    public ResponseEntity<String> sendEmail(@Valid @RequestBody SendNotificationRequest request) {
        UserEventOperation operation = UserEventOperation.fromString(request.operation());
        notificationService.sendByOperation(request.email(), operation);
        return ResponseEntity.ok("Notification sent");
    }
}
