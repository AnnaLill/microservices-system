package org.example.notification.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.notification.model.NotificationMessage;
import org.example.notification.service.EmailService;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Override
    public void send(String email, NotificationMessage messageType) {
        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setTo(email);
        mail.setSubject(messageType.getTitle());
        mail.setText(messageType.getMessage());
        mailSender.send(mail);
    }
}
