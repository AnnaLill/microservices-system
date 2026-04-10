package org.example.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/fallback")
public class FallbackController {
    public ResponseEntity<String> userServiceFallBack(){
        return ResponseEntity.ok("User service недоступен, попробуйте позже");
    }

    @GetMapping
    public ResponseEntity<String> notificationFallBack(){
        return ResponseEntity.ok("Notification service недоступен, попробуйте снова");
    }
}
