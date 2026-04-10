package org.example.notification.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.MailException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;

@RestControllerAdvice
public class NotificationExceptionHandler {

    @ExceptionHandler(MailAuthenticationException.class)
    public ProblemDetail handleMailAuth(MailAuthenticationException ex) {
        ProblemDetail detail = ProblemDetail.forStatusAndDetail(
                HttpStatus.SERVICE_UNAVAILABLE,
                "Не удалось войти на SMTP-сервер. Для Gmail нужен пароль приложения (App Password), "
                        + "если включена двухфакторная аутентификация; обычный пароль от аккаунта не подойдёт."
        );
        detail.setTitle("Ошибка аутентификации почты");
        detail.setType(URI.create("about:blank"));
        detail.setProperty("cause", ex.getMessage());
        return detail;
    }

    @ExceptionHandler(MailException.class)
    public ProblemDetail handleMail(MailException ex) {
        ProblemDetail detail = ProblemDetail.forStatusAndDetail(
                HttpStatus.SERVICE_UNAVAILABLE,
                "Ошибка отправки почты: " + ex.getMessage()
        );
        detail.setTitle("Ошибка отправки почты");
        detail.setType(URI.create("about:blank"));
        return detail;
    }
}
