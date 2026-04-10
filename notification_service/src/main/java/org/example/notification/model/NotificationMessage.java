package org.example.notification.model;

public enum NotificationMessage {
    CREATE_NOTIFICATION(
            "Создание аккаунта",
            "Здравствуйте! Ваш аккаунт на сайте ваш сайт был успешно создан"
    ),
    DELETE_NOTIFICATION(
            "Удаление аккаунта",
            "Здравствуйте! Ваш аккаунт был удалён"
    );

    private final String title;
    private final String message;

    NotificationMessage(String title, String message) {
        this.title = title;
        this.message = message;
    }

    public String getTitle() {
        return title;
    }

    public String getMessage() {
        return message;
    }
}
