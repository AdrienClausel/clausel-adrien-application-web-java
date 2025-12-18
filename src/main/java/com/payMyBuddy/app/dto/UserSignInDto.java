package com.payMyBuddy.app.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UserSignInDto(
        @Email(message = "Email invalide")
        @NotBlank(message = "L'email est obligatoire")
        String email,

        @NotBlank(message = "Le mot de passe est obligatoire")
        String password
) {
}