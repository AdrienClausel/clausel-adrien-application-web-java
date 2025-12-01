package com.payMyBuddy.app.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UserRelationDto(
        @Email(message = "Email invalide")
        @NotBlank(message = "L'email est obligatoire")
        String email,
        @NotBlank(message = "L'identifiant de l'utilisateur est obligatoire")
        Long userId
) {
}
