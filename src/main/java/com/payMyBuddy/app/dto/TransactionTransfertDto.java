package com.payMyBuddy.app.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record TransactionTransfertDto(
        @NotNull(message = "Vous devez choisir une relation")
        Long receiverId,
        @NotBlank(message = "La description est obligatoire")
        String description,
        String amount
) {
}
