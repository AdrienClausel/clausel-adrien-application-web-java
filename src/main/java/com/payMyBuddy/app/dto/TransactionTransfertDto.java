package com.payMyBuddy.app.dto;

import java.math.BigDecimal;

public record TransactionTransfertDto(
        Long senderId,
        Long receiverId,
        String description,
        BigDecimal amount
) {
}
