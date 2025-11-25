package com.ticketnotify.exception;

import java.time.Instant;

public record ApiError(
    boolean success,
    String errorCode,
    int status,
    String message,
    Instant timestamp
) {}