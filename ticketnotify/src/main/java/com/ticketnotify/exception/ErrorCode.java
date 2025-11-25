package com.ticketnotify.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {

    // --- GENERIC ---
    INTERNAL_SERVER_ERROR("ERR_SERVER_001", "Internal server error"),
    INVALID_INPUT("ERR_GEN_001", "Invalid input data"),
    RESOURCE_NOT_FOUND("ERR_GEN_002", "Resource not found"),

    // --- AUTH & SECURITY (JWT) ---
    UNAUTHORIZED("ERR_AUTH_001", "Unauthorized access"),
    FORBIDDEN("ERR_AUTH_002", "Access denied"),
    INVALID_CREDENTIALS("ERR_AUTH_003", "Invalid email or password"),
    TOKEN_EXPIRED("ERR_AUTH_004", "Token expired"),
    TOKEN_INVALID("ERR_AUTH_005", "Token invalid"),
    ACCOUNT_NOT_ACTIVE("ERR_AUTH_006", "Account not yet activated. Check your email"),

    // --- USER ---
    USER_NOT_FOUND("ERR_USER_001", "User not found"),
    EMAIL_ALREADY_EXISTS("ERR_USER_002", "This email is already registered"),

    // --- EVENT ---
    EVENT_NOT_FOUND("ERR_EVENT_001", "Event not found"),
    EVENT_FULL("ERR_EVENT_002", "Tickets sold out for this event"),
    EVENT_CANCELLED("ERR_EVENT_003", "This event has been cancelled"),
    EVENT_DATE_INVALID("ERR_EVENT_004", "The event date is invalid"),

    // --- TICKET ---
    TICKET_NOT_FOUND("ERR_TICKET_001", "Ticket not found"),
    TICKET_NOT_OWNER("ERR_TICKET_002", "You do not have permission to access this ticket"),
    REFUND_WINDOW_EXPIRED("ERR_TICKET_003", "The refund deadline has expired"),

    // --- EXTERNAL (KAFKA/EMAIL) ---
    NOTIFICATION_ERROR("ERR_EXT_001", "Failed to send notification");

    private final String code;
    private final String message;

    ErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }
}