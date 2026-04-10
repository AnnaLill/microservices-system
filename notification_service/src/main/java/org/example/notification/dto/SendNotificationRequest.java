package org.example.notification.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record SendNotificationRequest(
        @NotBlank @Email String email,
        @NotBlank String operation
) {
}
