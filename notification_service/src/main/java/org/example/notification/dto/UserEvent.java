package org.example.notification.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UserEvent(
        @NotBlank String operation,
        @NotBlank @Email String email
) {
}
