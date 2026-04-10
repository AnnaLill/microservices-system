package org.example;

import org.example.notification.NotificationServiceApplication;
import org.example.notification.dto.SendNotificationRequest;
import org.example.notification.dto.UserEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.web.client.RestTemplate;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;


@Testcontainers
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = NotificationServiceApplication.class
)
@ActiveProfiles("test")
class NotificationServiceApplicationTest {

    private static final RestTemplate SMTP4DEV_CLIENT = new RestTemplate();

    @Container
    static final KafkaContainer KAFKA = new KafkaContainer(
            DockerImageName.parse("confluentinc/cp-kafka:7.6.1")
    );

    @Container
    static final GenericContainer<?> SMTP4DEV = new GenericContainer<>(DockerImageName.parse("rnwood/smtp4dev:latest"))
            .withExposedPorts(25, 80);

    @DynamicPropertySource
    static void registerTestInfrastructure(DynamicPropertyRegistry registry) {
        registry.add("spring.kafka.bootstrap-servers", KAFKA::getBootstrapServers);

        registry.add("spring.mail.host", SMTP4DEV::getHost);
        registry.add("spring.mail.port", () -> String.valueOf(SMTP4DEV.getMappedPort(25)));
        registry.add("spring.mail.username", () -> "");
        registry.add("spring.mail.password", () -> "");
        registry.add("spring.mail.properties.mail.smtp.auth", () -> "false");
        registry.add("spring.mail.properties.mail.smtp.starttls.enable", () -> "false");
        registry.add("spring.mail.test-connection", () -> "false");
    }

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private KafkaTemplate<String, UserEvent> kafkaTemplate;

    private String smtp4devApiBase() {
        return "http://" + SMTP4DEV.getHost() + ":" + SMTP4DEV.getMappedPort(80);
    }

    @BeforeEach
    void clearMailbox() {
        try {
            String deleteAll = smtp4devApiBase() + "/api/Messages/" + URLEncoder.encode("*", StandardCharsets.UTF_8);
            SMTP4DEV_CLIENT.exchange(deleteAll, HttpMethod.DELETE, null, Void.class);
        } catch (Exception ignored) {

        }
    }

    private void awaitMessagesJsonContains(String substring) throws InterruptedException {
        String url = smtp4devApiBase() + "/api/Messages?pageSize=50";
        long deadline = System.currentTimeMillis() + Duration.ofSeconds(30).toMillis();
        while (System.currentTimeMillis() < deadline) {
            try {
                ResponseEntity<String> r = SMTP4DEV_CLIENT.getForEntity(url, String.class);
                String body = r.getBody();
                if (body != null && body.contains(substring)) {
                    return;
                }
            } catch (Exception ignored) {
                // контейнер только поднялся
            }
            Thread.sleep(300);
        }
        throw new AssertionError("За 30 с в smtp4dev не появилось ожидаемое содержимое: " + substring);
    }

    @Test
    @DisplayName("REST API /api/v1/notifications/email отправляет письмо о создании аккаунта")
    void shouldSendEmailViaApi() throws Exception {
        SendNotificationRequest request = new SendNotificationRequest("api-test@example.com", "CREATE");

        ResponseEntity<String> response = restTemplate.postForEntity(
                "/api/v1/notifications/email",
                request,
                String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        awaitMessagesJsonContains("Создание аккаунта");
        awaitMessagesJsonContains("api-test@example.com");
    }

    @Test
    @DisplayName("Kafka событие user-events отправляет письмо об удалении аккаунта")
    void shouldSendEmailViaKafkaEvent() throws Exception {
        kafkaTemplate.send("user-events", new UserEvent("DELETE", "kafka-test@example.com"))
                .get(30, TimeUnit.SECONDS);
        kafkaTemplate.flush();

        awaitMessagesJsonContains("Удаление аккаунта");
        awaitMessagesJsonContains("kafka-test@example.com");
    }
}
