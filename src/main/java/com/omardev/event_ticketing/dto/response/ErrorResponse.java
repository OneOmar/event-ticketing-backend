package com.omardev.event_ticketing.dto.response;

import java.time.LocalDateTime;
import java.util.List;

public record ErrorResponse(

        int status,
        String message,
        List<String> errors,
        LocalDateTime timestamp

) {}