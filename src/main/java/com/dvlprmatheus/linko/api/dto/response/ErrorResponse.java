package com.dvlprmatheus.linko.api.dto.response;

import java.time.OffsetDateTime;
import lombok.Builder;

@Builder
public record ErrorResponse(
    OffsetDateTime timestamp, int status, String error, String message, String path) {}
