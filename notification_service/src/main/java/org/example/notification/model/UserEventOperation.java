package org.example.notification.model;

public enum UserEventOperation {
    CREATE,
    DELETE;

    public static UserEventOperation fromString(String raw) {
        if (raw == null) {
            throw new IllegalArgumentException("Operation is required");
        }

        String op = raw.trim().toUpperCase();
        return switch (op) {
            case "CREATE", "CREATED", "USER_CREATE", "USER_CREATED", "CREATED_USER", "USER_ADDED", "ADD", "ADDED" ->
                    CREATE;
            case "DELETE", "DELETED", "USER_DELETE", "USER_DELETED", "DELETED_USER", "USER_REMOVED", "REMOVE", "REMOVED" ->
                    DELETE;
            default -> throw new IllegalArgumentException("Unsupported operation: " + raw);
        };
    }
}
