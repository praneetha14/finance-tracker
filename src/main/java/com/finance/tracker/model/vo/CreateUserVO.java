package com.finance.tracker.model.vo;

import java.time.LocalDateTime;

public record CreateUserVO(String apiKey, LocalDateTime createdAt, String createdBy) {
}
